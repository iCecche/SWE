package rowmapper;

import model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper extends RowMapper<Product> {

    @Override
    public Product mapRow (ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("name");
        String password = rs.getString("description");
        int price = rs.getInt("price");
        int stock = rs.getInt("stock_quantity");

        return new Product(id, username, password, price, stock);
    }
}
