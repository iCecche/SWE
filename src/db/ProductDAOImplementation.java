package db;

import model.Prodotto;
import rowmapper.ProductMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImplementation implements ProductDAO {

    private final DBManager db;
    private final ProductMapper mapper;

    public ProductDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new ProductMapper();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Prodotto> search() {
        String sql = "select * from PRODUCT";
        return db.execute_query(sql, mapper).getResults();
    }

    public List<Prodotto> search(String condition, Object... params) {
        String sql = "select * from PRODUCT WHERE " + condition;
        return db.execute_query(sql, mapper, params).getResults();
    }

    public List<Prodotto> searchAll() {
        return search();
    }

    public List<Prodotto> searchById(int id) {
        String condition = "id = ?";
        return search(condition, id);
    }

    @Override
    public void insert(String name, String description, Integer price, Integer stock_quantity) {
        String sql = "insert into PRODUCT (name, description, price, stock_quantity) values(?,?,?,?)";
        db.execute_query(sql, mapper, name, description, price, stock_quantity);
    }

    // creazione dinamica delle query
    @Override
    public void update(int id, String name, String description, Integer price, Integer stock) {
        String sql = "update PRODUCT SET ";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        List<Object> params = new ArrayList<>();

        if(name != null) {
            sqlBuilder.append("name = ?, ");
            params.add(name);
        }
        if (description != null) {
            sqlBuilder.append("description = ?, ");
            params.add(description);
        }
        if (price != null) {
            sqlBuilder.append("price = ?, ");
            params.add(price);
        }
        if (stock != null) {
            sqlBuilder.append("stock_quantity = ?");
            params.add(stock);
        }

        if(params.isEmpty()) {
            throw new IllegalArgumentException("Nothing to update!");
        }

        sqlBuilder.append(" where id = ?");
        params.add(id);
        db.execute_query(sqlBuilder.toString(), mapper, params.toArray());
    }

    @Override
    public void delete(int id) {
        String sql = "delete from PRODUCT where id = ?";
        db.execute_query(sql, mapper, id);
    }
}
