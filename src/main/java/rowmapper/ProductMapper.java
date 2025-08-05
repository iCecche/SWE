package rowmapper;

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

        return new Prodotto(id, username, password, price, stock);
    }
}
