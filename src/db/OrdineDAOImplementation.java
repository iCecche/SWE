package db;
import model.Ordine;
import model.DettaglioOrdine;
import model.DeliveryStatus;
import model.PaymentStatus;
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

    // TODO: choose interface or abstract class, implemented search methods must be private (searchAll... only accessible methods)
    @Override
    public QueryResult<Ordine> search(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    public List<Ordine> searchAll() {
        builder = QueryBuilder.create();
        builder.select("o.id AS order_id", "user_id", "date", "product_id", "quantity", "s.payment_status", "s.delivery_status")
                .from("ORDERS o")
                .leftJoin("ORDERS_STATUS s", "s.order_id = o.id")
                .leftJoin("ORDERS_DETAILS d", "d.order_id = o.id")
                .orderBy("o.id");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    public List<Ordine> searchByUserID(int user_id) {
        builder = QueryBuilder.create();
        builder.select("o.id AS order_id", "user_id", "date", "product_id", "quantity", "payment_status", "delivery_status")
                .from("ORDERS o")
                .leftJoin("ORDERS_STATUS s", "o.id = s.order_id")
                .leftJoin("ORDERS_DETAILS d", "d.order_id = o.id")
                .where("user_id = ?")
                .addParameter(user_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getResults();
    }

    @Override
    public QueryResult<Ordine> insert(Ordine order) {
        db.execute_transaction(() -> {
            Long newOrderId = newOrder(order.getUser_id(), order.getDate());
            newOrderStatus(newOrderId, order.getPaymentStatus(), order.getDeliveryStatus());
            for(DettaglioOrdine detail : order.getDetails()) {
                newOrderDetail(newOrderId, detail.getProduct_id(), detail.getQuantity()); //fixme: posso rimuovere orderId da DettaglioOrdine dato che adesso sono inclusi in Obj di tipo Ordine
            }
            return null;
        });
        return null;
    }

    private Long newOrder(int user_id, Date date) {
        builder = QueryBuilder.create();
        builder.insertInto("ORDERS", "user_id", "date")
                .values(user_id, date);

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

    private void newOrderStatus(Long order_id, PaymentStatus payment_status, DeliveryStatus delivery_status) {
        builder = QueryBuilder.create();
        builder.insertInto("ORDERS_STATUS", "order_id", "payment_status", "delivery_status")
                .withEnumColumn("payment_status", "payment_status_type")
                .withEnumColumn("delivery_status", "delivery_status_type")
                .values(order_id, payment_status.toString(), delivery_status.toString());

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();

        QueryResult<Ordine> order_status_result = db.execute_query(sql, mapper, order_id, params);

        if(order_status_result.getGeneratedKey().isEmpty()) {
            throw new RuntimeException("Error inserting order status!");
        }
    }

    public void insertNewOrder(Ordine order) {
        insert(order);
    }

    @Override
    public QueryResult<Ordine> update(String sql, Object... params) {;
        return db.execute_query(sql, mapper, params);
    }

    public void updateDeliveryStatus(int order_id, DeliveryStatus delivery_status) {
        builder = QueryBuilder.create();
        builder.update("ORDERS_STATUS")
                .set("delivery_status", delivery_status.toString(), true)
                .where("order_id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    public void updatePaymentStatus(int order_id, PaymentStatus payment_status) {
        builder = QueryBuilder.create();
        builder.update("ORDERS_STATUS")
                .set("payment_status", payment_status.toString(), true)
                .where("order_id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    @Override
    public QueryResult<Ordine> delete(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    public void deleteOrder(int order_id) {
        builder = QueryBuilder.create();
        builder.deleteFrom("ORDERS")
                .where("id = ?")
                .addParameter(order_id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        delete(sql, params);
    }
}
