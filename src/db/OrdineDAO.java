package db;

import model.DettaglioOrdine;
import model.Ordine;
import java.util.List;

public interface OrdineDAO {

    QueryResult<Ordine> search(String sql, Object... params);
    QueryResult<Ordine> insert(Ordine ordine);
    QueryResult<Ordine> update(String sql, Object... params);
    QueryResult<Ordine> delete(String sql, Object... params);

}
