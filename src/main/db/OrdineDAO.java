package main.db;

import main.model.Ordine;

public interface OrdineDAO {

    QueryResult<Ordine> search(String sql, Object... params);
    QueryResult<Ordine> insert(Ordine ordine);
    QueryResult<Ordine> update(String sql, Object... params);
    QueryResult<Ordine> delete(String sql, Object... params);

}
