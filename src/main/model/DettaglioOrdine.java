package main.model;

public class DettaglioOrdine {

    private int order_id;
    private int product_id;
    private int quantity;

    public DettaglioOrdine() {};

    public DettaglioOrdine(int product_id,  int quantity) {
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
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
