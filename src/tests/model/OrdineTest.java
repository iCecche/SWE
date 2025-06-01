package tests.model;

import main.model.DeliveryStatus;
import main.model.DettaglioOrdine;
import main.model.Ordine;
import main.model.PaymentStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class OrdineTest {

    @Test
    void createNewOrder() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);

        Assertions.assertEquals(0, order.getOrder_id());
        Assertions.assertEquals(0, order.getUser_id());
        Assertions.assertTrue(order.getDetails().isEmpty());
        Assertions.assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        Assertions.assertEquals(DeliveryStatus.PENDING, order.getDeliveryStatus());
    }

    // Getters
    @Test
    void getPaymentStatus() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Assertions.assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
    }

    @Test
    void getDeliveryStatus() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Assertions.assertEquals(DeliveryStatus.PENDING, order.getDeliveryStatus());
    }

    // Setters

    @Test
    void setOrder_id() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        int order_id = 1;

        order.setOrder_id(order_id);
        Assertions.assertEquals(order_id, order.getOrder_id());
    }

    @Test
    void setUser_id() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        int user_id = 1;

        order.setUser_id(user_id);
        Assertions.assertEquals(user_id, order.getUser_id());
    }

    @Test
    void setDate() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Date date = new Date();

        order.setDate(date);
        Assertions.assertEquals(date, order.getDate());
    }

    @Test
    void setDetail() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        DettaglioOrdine detail = new DettaglioOrdine(1, 1);

        order.setDetails(detail);
        Assertions.assertEquals(detail, order.getDetails().getFirst());
    }

    @Test
    void setDetails() {
        Ordine order = new Ordine(0, 0, null, new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        List<DettaglioOrdine> details = List.of(new DettaglioOrdine(1, 1), new DettaglioOrdine(2, 2));
        order.setDetails(details);

        Assertions.assertEquals(details, order.getDetails());
        Assertions.assertEquals(2, order.getDetails().size());
    }
}