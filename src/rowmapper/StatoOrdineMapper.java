package rowmapper;

import model.DeliveryStatus;
import model.PaymentStatus;
import model.StatoOrdine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatoOrdineMapper extends RowMapper<StatoOrdine> {

    int order_id;
    DeliveryStatus order_status;
    PaymentStatus payment_status;

    @Override
    public StatoOrdine mapRow(ResultSet rs) throws SQLException {
        if (contains(rs, "order_id"))
            order_id = rs.getInt("order_id");

        if(contains(rs, "stato_pagamento"))
            payment_status = PaymentStatus.valueOf(rs.getString("stato_pagamento"));

        if(contains(rs, "stato_consegna"))
            order_status = DeliveryStatus.valueOf(rs.getString("stato_consegna"));

        return new StatoOrdine(order_id, order_status, payment_status);
    }
}
