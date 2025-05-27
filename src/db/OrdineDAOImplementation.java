package db;
import model.Ordine;
import rowmapper.OrdineMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
        String sql = "SELECT o.id AS order_id, o.user_id, o.date as date, d.product AS product_id, d.quantity, s.stato_pagamento, s.stato_consegna " +
            "FROM ORDERS o " +
            "JOIN ORDERS_STATUS s ON o.id = s.order_id " +
            "JOIN ORDERS_DETAILS d ON d.order_id = o.id " +
            "ORDER BY o.id";
        return db.execute_statement(sql, mapper);
    }

    public List<Ordine> search(String condition, Object... params) {
        String sql = "SELECT o.id AS order_id, o.user_id, o.date as date, d.product AS product_id, d.quantity, s.stato_pagamento, s.stato_consegna " +
                "FROM ORDERS o " +
                "JOIN ORDERS_STATUS s ON o.id = s.order_id " +
                "JOIN ORDERS_DETAILS d ON d.order_id = o.id " +
                condition + " " +
                "ORDER BY o.id" ;
        return db.execute_statement(sql, mapper, params);
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
    public int insert(Object... params) {
        String sql = "INSERT INTO ORDERS (user_id, date) VALUES(?,?)";
        List<Ordine> orders = db.execute_statement(sql, mapper, params);
        return orders.getFirst().getOrder_id();
    }

    public int newOrder(int user_id) {
        Date date = new Date(System.currentTimeMillis());
        return insert(user_id, date);
    }

    @Override
    public void update(String update_fields, String condition, Object... params) {
        String sql = "UPDATE ORDERS set " + update_fields + condition;
        db.execute_statement(sql, mapper, params);
    }

    public void updateOrder(int user_id, Date date, int order_id) {
        String update_fields = "user_id = ?, date = ? ";
        String condition = "where id = ?";
        update(update_fields, condition, user_id, date, order_id);
    }

    @Override
    public void delete(String condition, Object... params) {
        String sql = "DELETE FROM ORDERS " + condition;
        db.execute_statement(sql, mapper, params);
    }

    public void deleteOrder(int order_id) {
        String condition = "where id = ?";
        delete(condition, order_id);
    }
}
