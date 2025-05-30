package model;
import java.util.Date;

public class Ordine {

    private int order_id;
    private int user_id;
    private Date date;
    int product_id;
    int quantity;
    PaymentStatus payment_status;
    DeliveryStatus delivery_status;

    public Ordine() {

    }


    public Ordine(int order_id, int user_id, Date date, int product_id, int quantity, PaymentStatus stato_pagamento, DeliveryStatus stato_consegna) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.date = date;
        this.product_id = product_id;
        this.quantity = quantity;
        this.payment_status = stato_pagamento;
        this.delivery_status = stato_consegna;
    }

    public void print() {
        System.out.println("OrderId: " + order_id);
        System.out.println("UserId: " + user_id);
        System.out.println("Date: " + date);
        System.out.println("ProductId: " + product_id);
        System.out.println("Order_Quantity: " + quantity);
        System.out.println("Order_Status: " + delivery_status);
        System.out.println("Payment_Status: " + payment_status);
        System.out.println();
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPayment_status() {
        return payment_status.toString();
    }

    public String getDelivery_status() {
        return delivery_status.toString();
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
}
