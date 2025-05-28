package db;

import model.StatoOrdine;
import rowmapper.StatoOrdineMapper;

import java.sql.SQLException;

public class StatoOrdineDAOImplementation implements StatoOrdineDAO {

    private final DBManager db;
    private final StatoOrdineMapper mapper;

    public StatoOrdineDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new StatoOrdineMapper();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(int order_id, String order_status, String payment_status) {
        String sql = "INSERT INTO ORDERS_STATUS (order_id, stato_pagamento, stato_consegna) VALUES(?, ?::payment_status, ?::order_status)";
        db.execute_statement(sql, mapper, order_id, order_status, payment_status);
    }

    public void inserOrderStatus(StatoOrdine order_status) {
        insert(order_status.getId(), order_status.getOrder_status().toString(), order_status.getPayment_status().toString());
    }

    @Override
    public void update(int order_id, String order_status, String payment_status) {

    }
}
