package rowmapper;

import model.DettaglioOrdine;
import model.Ordine;
import model.Product;
import model.StatoOrdine;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;
import java.sql.SQLException;

public class OrdineMapper extends RowMapper<Ordine> {

    @Override
    public Ordine mapRow (ResultSet rs) throws SQLException {

        int order_id = rs.getInt("order_id");
        int user_id = rs.getInt("user_id");
        Date date = rs.getDate("date");

        int product_id = rs.getInt("product_id");
        String product_name = rs.getString("product_name");
        String description = rs.getString("product_name");
        int price = rs.getInt("product_price");
        int stock_quantity = rs.getInt("product_stock_quantity");
        Product product = new Product(product_id, product_name, description, price, stock_quantity);

        int quantity = rs.getInt("order_quantity");
        DettaglioOrdine dettaglio = new DettaglioOrdine(order_id, product, quantity);

        String order_status = rs.getString("order_status");
        String payment_status = rs.getString("payment_status");
        StatoOrdine stato = new StatoOrdine(order_id, order_status, payment_status);

        return new Ordine(order_id, user_id, date, dettaglio, stato);
    }
}