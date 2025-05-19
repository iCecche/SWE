package db;
import model.Ordine;
import rowmapper.OrdineMapper;

import java.sql.SQLException;
import java.util.Arrays;
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
        String sql = "SELECT" + " O.id as order_id, O.user_id as user_id, O.date as date, P.id as product_id, P.name as product_name, P.description as product_description, P.price as product_price, P.stock_quantity as product_stock_quantity, OD.quantity as order_quantity, OS.stato_consegna as order_status, OS.stato_pagamento as payment_status" +
                " FROM ORDERS as O" +
                " JOIN ORDERS_DETAILS as OD ON OD.id = O.id" +
                " JOIN PRODUCT as P ON P.id = OD.product" +
                " JOIN ORDERS_STATUS as OS ON O.stato_ordine = OS.id";
        return db.execute_statement(sql, mapper);
    }

    public List<Ordine> search(String condition, Object... params) {
        String sql = "SELECT" + " O.id as order_id, O.user_id as user_id, O.date as date, P.id as product_id, P.name as product_name, P.description as product_description, P.price as product_price, P.stock_quantity as product_stock_quantity, OD.quantity as order_quantity, OS.stato_consegna as order_status, OS.stato_pagamento as payment_status" +
                " FROM ORDERS as O" +
                " JOIN ORDERS_DETAILS as OD ON OD.id = O.id" +
                " JOIN PRODUCT as P ON P.id = OD.product" +
                " JOIN ORDERS_STATUS as OS ON O.stato_ordine = OS.id " +
                condition;
        return db.execute_statement(sql, mapper, params);
    }

    public List<Ordine> searchAll() {
        return search();
    }

    public List<Ordine> searchByUserID(int user_id) {
        return search("WHERE O.user_id = ?", user_id);
    }

    public List<Ordine> searchByProductID(int product_id) {
        return search("WHERE P.id = ?", product_id);
    }

    public List<Ordine> searchByProductName(String product_name) {
        product_name = product_name.toLowerCase();
        return search("WHERE P.name = ?", product_name);
    }

    @Override
    public void insert() {
        String sql = "INSERT INTO ORDERS (id_cliente, data, stato_ordine, dettaglio_ordine) VALUES(?,?,?,?)";
        db.execute_statement(sql, mapper);
    }

    @Override
    public void update() {
        String sql = "UPDATE ORDERS set id_cliente = ?, data = ?, stato_ordine = ?, dettaglio_ordine = ? where id_cliente = ?";
        db.execute_statement(sql, mapper);
    }

    @Override
    public void delete() {
        String sql = "DELETE FROM ORDERS where id = ?";
        db.execute_statement(sql, mapper);
    }
}
