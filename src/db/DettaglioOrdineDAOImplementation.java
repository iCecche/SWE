package db;

import model.DettaglioOrdine;
import rowmapper.DettaglioOrdineMapper;

import java.sql.SQLException;
import java.util.List;

public class DettaglioOrdineDAOImplementation implements DettaglioOrdineDAO {

    private final DBManager db;
    private final DettaglioOrdineMapper mapper;

    public DettaglioOrdineDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new DettaglioOrdineMapper();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Object... params) {
        String sql = "INSERT INTO ORDERS_DETAILS (order_id, product_id, quantity) VALUES(?,?,?)";
        db.execute_query(sql, mapper, params);
    }

    public void newOrderDetail(DettaglioOrdine order_detail) {
        insert(order_detail.getOrder_id(), order_detail.getProduct_id(), order_detail.getQuantity());
    }


    @Override
    public void update(String update_fields, String condition, Object... params) {

    }
}
