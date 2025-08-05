package db;

import model.Ordine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;

import java.util.List;

public interface OrdineDAO {

    // Search
    List<Ordine> searchAll();
    List<Ordine> searchByUserID(int user_id);

    // Insert
    void insertNewOrder(Ordine order);

    // Update
    void updateDeliveryStatus(int order_id, DeliveryStatus delivery_status);
    void updatePaymentStatus(int order_id, PaymentStatus payment_status);

    // Delete
    void deleteOrder(int order_id);

}
