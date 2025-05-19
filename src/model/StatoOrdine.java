package model;

public class StatoOrdine {

    public StatoOrdine(int order_id, String order_status, String payment_status) {
        this.order_id = order_id;
        this.order_status = order_status;
        this.payment_status = payment_status;
    }

    private int order_id;
    private String order_status;
    private String payment_status;

    public int getId() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getPayment_status() {
        return payment_status;
    }
}
