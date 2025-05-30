package model;

public class StatoOrdine {

    private int order_id;
    private DeliveryStatus delivery_status;
    private PaymentStatus payment_status;

    public StatoOrdine() {
        this.payment_status = PaymentStatus.PENDING;
        this.delivery_status = DeliveryStatus.PENDING;
    }

    public StatoOrdine(int order_id, DeliveryStatus order_status, PaymentStatus payment_status) {
        this.order_id = order_id;
        this.delivery_status = order_status;
        this.payment_status = payment_status;
    }

    public int getId() {
        return order_id;
    }

    public DeliveryStatus getDelivery_status() {
        return delivery_status;
    }

    public PaymentStatus getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(PaymentStatus payment_status) {
        this.payment_status = payment_status;
    }

    public void setDelivery_status(DeliveryStatus delivery_status) {
        this.delivery_status = delivery_status;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
