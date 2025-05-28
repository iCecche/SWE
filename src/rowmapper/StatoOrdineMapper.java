package rowmapper;

import model.OrderStatus;
import model.PaymentStatus;
import model.StatoOrdine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatoOrdineMapper extends RowMapper<StatoOrdine> {

    @Override
    public StatoOrdine mapRow(ResultSet rs) throws SQLException {
        int order_id = rs.getInt("order_id");
        OrderStatus order_status = OrderStatus.valueOf(rs.getString("stato_pagamento"));
        PaymentStatus payment_status = PaymentStatus.valueOf(rs.getString("stato_consegna"));

        return new StatoOrdine(order_id, order_status, payment_status);
    }
}
