package orm.rowmapper;

import model.Prodotto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper extends RowMapper<Prodotto> {

    @Override
    public Prodotto mapRow (ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("name");
        String password = rs.getString("description");
        int price = rs.getInt("price");
        int stock = rs.getInt("stock_quantity");
        boolean is_deleted = rs.getBoolean("is_deleted");

        return new Prodotto(id, username, password, price, stock, is_deleted);
    }
}
