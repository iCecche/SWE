package db;

public interface StatoOrdineDAO {

    void insert(int order_id, String order_status, String payment_status);
    void update(int order_id, String order_status, String payment_status);
}
