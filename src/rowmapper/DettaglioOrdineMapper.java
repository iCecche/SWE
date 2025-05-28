package rowmapper;

import model.DettaglioOrdine;

public class DettaglioOrdineMapper extends RowMapper<DettaglioOrdine> {

    @Override
    public DettaglioOrdine mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        int order_id = rs.getInt("order_id");
        int product_id = rs.getInt("product_id");
        int quantity = rs.getInt("quantity");

        return new DettaglioOrdine(order_id, product_id, quantity);
    }
}
