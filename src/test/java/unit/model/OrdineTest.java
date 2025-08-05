package unit.model;

import model.enums.DeliveryStatus;
import model.DettaglioOrdine;
import model.Ordine;
import model.enums.PaymentStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class OrdineTest {

    private Ordine order;

    @BeforeEach
    void setUp() {
        order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
    }

    @Test
    void createNewOrder() {
        Assertions.assertEquals(0, order.getOrder_id());
        Assertions.assertEquals(0, order.getUser_id());
        Assertions.assertTrue(order.getDetails().isEmpty());
        Assertions.assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        Assertions.assertEquals(DeliveryStatus.PENDING, order.getDeliveryStatus());
    }

    // Getters
    @Test
    void getPaymentStatus() {
        Assertions.assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
    }

    @Test
    void getDeliveryStatus() {
        Assertions.assertEquals(DeliveryStatus.PENDING, order.getDeliveryStatus());
    }

    // Setters

    @Test
    void setOrder_id() {
        int order_id = 1;
        order.setOrder_id(order_id);

        Assertions.assertEquals(order_id, order.getOrder_id());
    }

    @Test
    void setUser_id() {
        int user_id = 1;
        order.setUser_id(user_id);

        Assertions.assertEquals(user_id, order.getUser_id());
    }

    @Test
    void setDate() {
        Date date = new Date();
        order.setDate(date);

        Assertions.assertEquals(date, order.getDate());
    }

    @Test
    void setDetail() {
        DettaglioOrdine detail = new DettaglioOrdine(1, 1);
        order.setDetails(detail);

        Assertions.assertEquals(detail, order.getDetails().getFirst());
    }

    @Test
    void setDetails() {
        List<DettaglioOrdine> details = List.of(new DettaglioOrdine(1, 1), new DettaglioOrdine(2, 2));
        order.setDetails(details);

        Assertions.assertEquals(details, order.getDetails());
        Assertions.assertEquals(2, order.getDetails().size());
    }
}