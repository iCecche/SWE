package model;

public class DettaglioOrdine {

    private int order_id;
    private int product_id;
    private int quantity;

    public DettaglioOrdine() {};

    public DettaglioOrdine(int order_id, int product_id,  int quantity) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
