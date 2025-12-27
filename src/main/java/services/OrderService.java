package services;

import model.Ordine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import orm.OrdineDAOImplementation;

import java.util.List;

public class OrderService {
    private final OrdineDAOImplementation ordineDAO;

    public OrderService() {
        ordineDAO = new OrdineDAOImplementation();
    }

    public List<Ordine> getOrders() {
        return ordineDAO.searchAll();
    }

    public List<Ordine> getOrdersByUserID(int user_id) {
        return ordineDAO.searchByUserID(user_id);
    }

    public void createOrder(Ordine ordine) {
        ordineDAO.insertNewOrder(ordine);
    }

    public void updatePaymentStatus(int order_id, PaymentStatus new_status) {
        ordineDAO.updatePaymentStatus(order_id, new_status);
    }

    public void updateDeliveryStatus(int order_id, DeliveryStatus new_status) {
        ordineDAO.updateDeliveryStatus(order_id, new_status);
    }

    public void deleteOrder(int order_id) {
        ordineDAO.deleteOrder(order_id);
    }
}
