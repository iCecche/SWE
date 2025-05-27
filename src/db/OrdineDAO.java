package db;

import model.Ordine;
import java.util.List;

public interface OrdineDAO {

    List<Ordine> search();
    int insert(Object... params);
    void update(String update_fields, String condition, Object... params);
    void delete(String condition, Object... params);

}
