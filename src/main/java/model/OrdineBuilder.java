package model;

import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdineBuilder {
    private int order_id;
    private int user_id;
    private Date date;
    private List<DettaglioOrdine> details;
    private PaymentStatus payment_status;
    private DeliveryStatus delivery_status;

    private OrdineBuilder() {
        details = new ArrayList<>();
    }

    public static OrdineBuilder create() {
        return new OrdineBuilder();
    }

    public void withOrderId(int order_id) {
        this.order_id = order_id;
    }

    public void withUserId(int user_id) {
        this.user_id = user_id;
    }

    public void withDate(Date date) {
        this.date = date;
    }

    public void withDetails(List<DettaglioOrdine> details) {
        this.details = details;
    }

    public void withDetails(DettaglioOrdine detail) {
        this.details.add(detail);
    }

    public void withPaymentStatus(PaymentStatus payment_status) {
        this.payment_status = payment_status;
    }

    public void withDeliveryStatus(DeliveryStatus delivery_status) {
        this.delivery_status = delivery_status;
    }

    public Ordine build() {
        return new Ordine(order_id, user_id, date, details, payment_status, delivery_status);
    }
}
