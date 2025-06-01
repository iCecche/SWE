package main.db;

import main.model.Prodotto;
import main.rowmapper.ProductMapper;
import java.sql.SQLException;
import java.util.List;

public class ProductDAOImplementation implements ProductDAO {

    private final DBManager db;
    private final ProductMapper mapper;
    private QueryBuilder builder;

    public ProductDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new ProductMapper();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QueryResult<Prodotto> search(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    public List<Prodotto> searchAll() {
        builder = QueryBuilder.create();
        builder.select()
                .from("PRODUCT");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    public Prodotto searchById(int id) {
        builder = QueryBuilder.create();
        builder.select()
                .from("PRODUCT")
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getSingleResult().orElseThrow();
    }

    @Override
    public void insert(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }

    public void insertNewProduct(String name, String description, Integer price, Integer stock_quantity) {
        QueryBuilder builder = QueryBuilder.create();
        builder.insertInto("PRODUCT", "name", "description", "price", "stock_quantity")
                .values(name, description, price, stock_quantity);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        insert(sql, params);
    }

    // creazione dinamica delle query
    @Override
    public void update(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }

    public void updateProduct(int id, String name, String description, Integer price, Integer stock) {
        builder = QueryBuilder.create();
        builder.update("PRODUCT")
                .set("name", name)
                .set("description", description)
                .set("price", price)
                .set("stock_quantity", stock)
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    @Override
    public void delete(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }

    public void deleteProduct(int id) {
        builder = QueryBuilder.create();
        builder.deleteFrom("PRODUCT")
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        delete(sql, params);
    }
}
