package model;

public class DettaglioOrdine {

    public DettaglioOrdine(int order_id, Product product, int quantity) {
        this.order_id = order_id;
        this.product = product;
        this.quantity = quantity;
    }

    private int order_id;
    private Product product;
    private int quantity;

    public int getOrder_id() {
        return order_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
