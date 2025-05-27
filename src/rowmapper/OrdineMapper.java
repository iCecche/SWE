package rowmapper;

import model.DettaglioOrdine;
import model.Ordine;
import model.Prodotto;
import model.StatoOrdine;

import java.sql.ResultSet;
import java.util.Date;
import java.sql.SQLException;

public class OrdineMapper extends RowMapper<Ordine> {

    @Override
    public Ordine mapRow (ResultSet rs) throws SQLException {

        if(contains(rs, "order_id")) {
            int order_id = rs.getInt("order_id");
            int user_id = rs.getInt("user_id");
            Date date = rs.getDate("date");
            int product_id = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");
            String stato_pagamento = rs.getString("stato_pagamento");
            String stato_consegna = rs.getString("stato_consegna");

            return new Ordine(order_id, user_id, date, product_id, quantity, stato_pagamento, stato_consegna);
        }else {
            int order_id = rs.getInt(1);
            return new Ordine(order_id);
        }

    }
}