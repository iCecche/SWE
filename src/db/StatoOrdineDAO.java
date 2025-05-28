package db;

public interface StatoOrdineDAO {

    void insert(int order_id, String order_status, String payment_status);
    void update(String update_fields, String condition, Object... params);
}
