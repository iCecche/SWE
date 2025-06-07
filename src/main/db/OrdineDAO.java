package main.db;

import main.model.Ordine;
import main.model.enums.DeliveryStatus;
import main.model.enums.PaymentStatus;

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
