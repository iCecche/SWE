package db;

import model.Prodotto;
import rowmapper.ProductMapper;
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

    public ProductDAOImplementation(DBManager db) {
        this.db = db;
        mapper = new ProductMapper();
    }

    @Override
    public List<Prodotto> searchAll() {
        builder = QueryBuilder.create();
        builder.select()
                .from("PRODUCT");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    @Override
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

    private QueryResult<Prodotto> search(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    @Override
    public void insertNewProduct(String name, String description, Integer price, Integer stock_quantity) {
        QueryBuilder builder = QueryBuilder.create();
        builder.insertInto("PRODUCT", "name", "description", "price", "stock_quantity")
                .values(name, description, price, stock_quantity);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        insert(sql, params);
    }

    private void insert(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }

    @Override
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

    private void update(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }

    @Override
    public void deleteProduct(int id) {
        builder = QueryBuilder.create();
        builder.update("PRODUCT")
                .set("is_deleted", true)
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        delete(sql, params);
    }

    private void delete(String sql, Object... params) {
        db.execute_query(sql, mapper, params);
    }
}
