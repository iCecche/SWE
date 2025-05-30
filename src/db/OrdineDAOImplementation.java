package db;
import model.*;
import rowmapper.DettaglioOrdineMapper;
import rowmapper.OrdineMapper;
import rowmapper.StatoOrdineMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrdineDAOImplementation implements OrdineDAO {
    private final DBManager db;
    private final OrdineMapper mapper;

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
    public List<Ordine> search() {
        String sql = "SELECT o.id AS order_id, o.user_id, o.date as date, d.product_id AS product_id, d.quantity, s.stato_pagamento, s.stato_consegna " +
            "FROM ORDERS o " +
            "LEFT JOIN ORDERS_STATUS s ON o.id = s.order_id " +
            "LEFT JOIN ORDERS_DETAILS d ON d.order_id = o.id " + // FIXME: JOIN TYPE
            "ORDER BY o.id";
        return db.execute_query(sql, mapper).getResults();
    }

    public List<Ordine> search(String condition, Object... params) {
        String sql = "SELECT o.id AS order_id, o.user_id, o.date as date, d.product_id AS product_id, d.quantity, s.stato_pagamento, s.stato_consegna " +
                "FROM ORDERS o " +
                "LEFT JOIN ORDERS_STATUS s ON o.id = s.order_id " +
                "LEFT JOIN ORDERS_DETAILS d ON d.order_id = o.id " +
                condition + " " +
                "ORDER BY o.id" ;
        return db.execute_query(sql, mapper, params).getResults();
    }

    public List<Ordine> searchAll() {
        return search();
    }

    public List<Ordine> searchByUserID(int user_id) {
        return search("WHERE user_id = ?", user_id);
    }

    public List<Ordine> searchByProductID(int product_id) {
        return search("WHERE prodotto = ?", product_id);
    }

    public List<Ordine> searchByProductName(String product_name) {
        product_name = product_name.toLowerCase();
        return search("WHERE P.name = ?", product_name);
    }

    @Override
    public void insert(Ordine order) {
        db.execute_transaction(() -> {
            Long newOrderId = newOrder(order.getUser_id(), order.getDate());
            newOrderStatus(newOrderId, order.getPaymentStatus(), order.getDeliveryStatus());
            for(DettaglioOrdine detail : order.getDetails()) {
                newOrderDetail(newOrderId, detail.getProduct_id(), detail.getQuantity()); //fixme: posso rimuovere orderId da DettaglioOrdine dato che adesso sono inclusi in Obj di tipo Ordine
            }
            return null;
        });
    }

    private Long newOrder(int user_id, Date date) {
        String sql = "INSERT INTO ORDERS (user_id, date) VALUES(?,?)";
        QueryResult<Ordine> order_result = db.execute_query(sql, mapper, user_id, date);
        Long orderId = order_result.getGeneratedKey().orElse(null);

        if(orderId == null) {
            throw new RuntimeException("Error inserting order!");
        }

        return orderId;
    }

    private void newOrderDetail(Long order_id, int product_id, int quantity) {
        String sql = "INSERT INTO ORDERS_DETAILS (order_id, product_id, quantity) VALUES(?,?,?)";
        db.execute_query(sql, mapper, order_id, product_id, quantity);
    }

    private void newOrderStatus(Long order_id, PaymentStatus payment_status, DeliveryStatus delivery_status) {
        String sql2 = "INSERT INTO ORDERS_STATUS (order_id, stato_pagamento, stato_consegna) VALUES(?, ?::payment_status, ?::delivery_status)";
        QueryResult<Ordine> order_status_result = db.execute_query(sql2, mapper, order_id, payment_status.toString(), delivery_status.toString());

        if(order_status_result.getGeneratedKey().isEmpty()) {
            throw new RuntimeException("Error inserting order status!");
        }
    }

    public void insertNewOrder(Ordine order) {
        insert(order);
    }

    @Override
    public void update(String update_fields, String condition, Object... params) {
        String sql = "UPDATE ORDERS_STATUS SET " + update_fields + " WHERE " + condition;
        db.execute_query(sql, mapper, params);
    }

    public void updateDeliveryStatus(int order_id, DeliveryStatus delivery_status) {
        update("stato_consegna = ?::delivery_status ", "order_id = ?", delivery_status.toString(), order_id );
    }

    public void updatePaymentStatus(int order_id, PaymentStatus payment_status) {
        update("stato_pagamento = ?::payment_status ", "order_id = ?", payment_status.toString(), order_id);
    }

    @Override
    public void delete(String condition, Object... params) {
        String sql = "DELETE FROM ORDERS " + condition;
        db.execute_query(sql, mapper, params);
    }

    public void deleteOrder(int order_id) {
        String condition = "where id = ?";
        delete(condition, order_id);
    }
}
