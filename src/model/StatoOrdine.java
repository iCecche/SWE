package model;

public class StatoOrdine {

    private int order_id;
    private OrderStatus order_status;
    private PaymentStatus payment_status;

    public StatoOrdine() {
        this.payment_status = PaymentStatus.PENDING;
        this.order_status = OrderStatus.PENDING;
    }

    public StatoOrdine(int order_id, OrderStatus order_status, PaymentStatus payment_status) {
        this.order_id = order_id;
        this.order_status = order_status;
        this.payment_status = payment_status;
    }

    public int getId() {
        return order_id;
    }

    public OrderStatus getOrder_status() {
        return order_status;
    }

    public PaymentStatus getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(PaymentStatus payment_status) {
        this.payment_status = payment_status;
    }

    public void setOrder_status(OrderStatus order_status) {
        this.order_status = order_status;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
