package services;

import model.Ordine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import model.exceptions.OrderBusinessException;
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

    public void deleteOrder(int order_id) {
        ordineDAO.deleteOrder(order_id);
    }

    public void shipOrder(int order_id) {
       Ordine ordine = ordineDAO.searchById(order_id);

       // Non puoi spedire se ordine deve essere pagato
       if(ordine.getPaymentStatus() != PaymentStatus.PAID) {
           throw new OrderBusinessException("Impossibile inoltrare l'ordine #" + order_id + ": il pagamento risulta ancora " + ordine.getPaymentStatus());
       }

        // Non puoi rispedire se è già consegnato o annullato
        if (ordine.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new OrderBusinessException("L'ordine non è in uno stato che permette l'inoltro.");
        }

        // Se tutti i controlli passano, procedo
        ordineDAO.updateDeliveryStatus(order_id, DeliveryStatus.SHIPPED);
    }

    public void payOrder(int order_id) {
        Ordine ordine = ordineDAO.searchById(order_id);

        // Non puoi ripagare se ordine risulta già pagato
        if(ordine.getPaymentStatus() == PaymentStatus.PAID) {
            throw new OrderBusinessException("Impossibile inoltrare l'ordine #\" + order_id + \": il pagamento risulta già effettuato");
        }
        // se tutti i controlli passano, procedo
        ordineDAO.updatePaymentStatus(order_id, PaymentStatus.PAID);
    }
}
