package db;

public interface DettaglioOrdineDAO {
    void insert(Object... params);
    void update(String update_fields, String condition, Object... params);
}
