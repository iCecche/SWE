package main.model;

import main.model.enums.DeliveryStatus;
import main.model.enums.PaymentStatus;

import java.util.Date;
import java.util.List;

public class Ordine {

    private int order_id;
    private int user_id;
    private Date date;
    private List<DettaglioOrdine> details;
    private PaymentStatus payment_status;
    private DeliveryStatus delivery_status;

    public Ordine(int order_id, int user_id, Date date, List<DettaglioOrdine> details, PaymentStatus stato_pagamento, DeliveryStatus stato_consegna) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.date = date;
        this.details = details;
        this.payment_status = stato_pagamento;
        this.delivery_status = stato_consegna;
    }

    public void print() {
        System.out.println("OrderId: " + order_id);
        System.out.println("UserId: " + user_id);
        System.out.println("Date: " + date);
        System.out.println("Order_Status: " + delivery_status);
        System.out.println("Payment_Status: " + payment_status);
        for( DettaglioOrdine detail : details) {
            System.out.println("Product: " + detail.getProduct_id());
            System.out.println("Quantity: " + detail.getQuantity());
        }
        System.out.println();
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public Date getDate() {
        return date;
    }

    public List<DettaglioOrdine> getDetails() {
        return details;
    }

    public String getPayment_status() {
        return payment_status.toString();
    }

    public String getDelivery_status() {
        return delivery_status.toString();
    }

    public PaymentStatus getPaymentStatus() {
        return payment_status;
    }

    public DeliveryStatus getDeliveryStatus() {
        return delivery_status;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDetails(List<DettaglioOrdine> details) {
        this.details = details;
    }

    public void setDetails(DettaglioOrdine detail) {
        this.details.add(detail);
    }
}
