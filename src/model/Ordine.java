package model;
import java.util.Date;

public class Ordine {

    private int order_id;
    private int user_id;
    private Date date;
    int product_id;
    int quantity;
    String stato_pagamento;
    String stato_consegna;

    public Ordine(int order_id) {
        this.order_id = order_id;
    }

    public Ordine(int order_id, int user_id, Date date, int product_id, int quantity, String stato_pagamento, String stato_consegna) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.date = date;
        this.product_id = product_id;
        this.quantity = quantity;
        this.stato_pagamento = stato_pagamento;
        this.stato_consegna = stato_consegna;
    }

    public void print() {
        System.out.println("OrderId: " + order_id);
        System.out.println("UserId: " + user_id);
        System.out.println("Date: " + date);
        System.out.println("ProductId: " + product_id);
        System.out.println("Order_Quantity: " + quantity);
        System.out.println("Order_Status: " + stato_consegna);
        System.out.println("Payment_Status: " + stato_pagamento);
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

    public String getStato_pagamento() {
        return stato_pagamento;
    }

    public String getStato_consegna() {
        return stato_consegna;
    }
}
