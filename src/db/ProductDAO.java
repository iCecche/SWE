package db;

import model.Prodotto;

public interface ProductDAO {

    QueryResult<Prodotto> search(String sql, Object... params);

    void insert(String sql, Object... params);

    void update(String sql, Object... params);

    void delete(String sql, Object... params);
}
