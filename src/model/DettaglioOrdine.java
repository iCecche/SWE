package model;

public class DettaglioOrdine {

    public DettaglioOrdine(int order_id, Prodotto product, int quantity) {
        this.order_id = order_id;
        this.product = product;
        this.quantity = quantity;
    }

    private int order_id;
    private Prodotto product;
    private int quantity;

    public int getOrder_id() {
        return order_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Prodotto getProduct() {
        return product;
    }
}
