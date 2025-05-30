package db;

import model.DettaglioOrdine;
import model.Ordine;
import java.util.List;

public interface OrdineDAO {

    List<Ordine> search();
    void insert(Ordine order);
    void update(String update_fields, String condition, Object... params);
    void delete(String condition, Object... params);

}
