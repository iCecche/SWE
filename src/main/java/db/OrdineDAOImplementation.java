package db;
import model.Ordine;
import model.DettaglioOrdine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import rowmapper.OrdineMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class OrdineDAOImplementation implements OrdineDAO {
    private final DBManager db;
    private final OrdineMapper mapper;
    private QueryBuilder builder;

    public OrdineDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new OrdineMapper();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OrdineDAOImplementation(DBManager db) {
        this.db = db;
        mapper = new OrdineMapper();
    }

    @Override
    public List<Ordine> searchAll() {
        builder = QueryBuilder.create();
        builder.select("o.id AS order_id", "user_id", "date", "product_id", "quantity", "payment_status", "delivery_status")
                .from("ORDERS o")
                .leftJoin("ORDERS_DETAILS d", "d.order_id = o.id")
                .orderBy("o.id");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    @Override
    public List<Ordine> searchByUserID(int user_id) {
        builder = QueryBuilder.create();
        builder.select("o.id AS order_id", "user_id", "date", "product_id", "quantity", "payment_status", "delivery_status")
                .from("ORDERS o")
                .leftJoin("ORDERS_DETAILS d", "d.order_id = o.id")
                .where("user_id = ?")
                .addParameter(user_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getResults();
    }

    private QueryResult<Ordine> search(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    @Override
    public Long insertNewOrder(Ordine order) {
        return db.execute_transaction(() -> {
            Long new_order_id = newOrder(order.getUser_id(), order.getDate(), order.getDeliveryStatus(), order.getPaymentStatus());
            updateStock(order.getDetails());
            for(DettaglioOrdine detail : order.getDetails()) {
                newOrderDetail(new_order_id, detail.getProduct_id(), detail.getQuantity()); //fixme: posso rimuovere orderId da DettaglioOrdine dato che adesso sono inclusi in Obj di tipo Ordine
            }
            return new_order_id;
        });
    }

    private Long newOrder(int user_id, Date date, DeliveryStatus delivery_status, PaymentStatus payment_status) {
        builder = QueryBuilder.create();
        builder.insertInto("ORDERS", "user_id", "date", "delivery_status", "payment_status")
                .withEnumColumn("payment_status", "payment_status_type")
                .withEnumColumn("delivery_status", "delivery_status_type")
                .values(user_id, date, delivery_status.toString(), payment_status.toString());

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();

        QueryResult<Ordine> order_result = db.execute_query(sql, mapper, params);
        Long orderId = order_result.getGeneratedKey().orElse(null);

        if(orderId == null) {
            throw new RuntimeException("Error inserting order!");
        }

        return orderId;
    }

    private void newOrderDetail(Long order_id, int product_id, int quantity) {
        builder = QueryBuilder.create();
        builder.insertInto("ORDERS_DETAILS", "order_id", "product_id", "quantity")
                .values(order_id, product_id, quantity);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        db.execute_query(sql, mapper, params);
    }

    private void updateStock(List<DettaglioOrdine> orderDetails) {
        String sql = "UPDATE PRODUCT SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        for ( DettaglioOrdine detail : orderDetails) {
            try {
                db.execute_query(sql, mapper, detail.getQuantity(), detail.getProduct_id(), detail.getQuantity());
            }catch (Exception e) {
                throw new RuntimeException("Stock insufficiente per il prodotto ID: " + detail.getProduct_id());
            }
        }
    }

    @Override
    public void updateDeliveryStatus(int order_id, DeliveryStatus delivery_status) {
        builder = QueryBuilder.create();
        builder.update("ORDERS")
                .set("delivery_status", delivery_status.toString(), true)
                .where("id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    @Override
    public void updatePaymentStatus(int order_id, PaymentStatus payment_status) {
        builder = QueryBuilder.create();
        builder.update("ORDERS o")
                .set("payment_status", payment_status.toString(), true)
                .where("id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    private void update(String sql, Object... params) {;
        db.execute_query(sql, mapper, params);
    }

    @Override
    public void deleteOrder(int order_id) {
        builder = QueryBuilder.create();
        builder.deleteFrom("ORDERS")
                .where("id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        delete(sql, params);
    }

    private void delete(String sql, Object... params) {
         db.execute_query(sql, mapper, params);
    }
}
