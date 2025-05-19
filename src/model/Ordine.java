package model;
import java.util.Date;

public class Ordine {

    public Ordine(int id, int user_id, Date date) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
    }

    public Ordine(int id, int user_id, Date date, DettaglioOrdine dettaglio, StatoOrdine stato) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.dettaglio = dettaglio;
        this.stato = stato;
    }

    public void print() {
        System.out.println("OrderId: " + id);
        System.out.println("UserId: " + user_id);
        System.out.println("Date: " + date);
        System.out.println("ProductId: " + dettaglio.getProduct().getId());
        System.out.println("Product_Name: " + dettaglio.getProduct().getName());
        System.out.println("Product_Description: " + dettaglio.getProduct().getDescription());
        System.out.println("Product_Price: " + dettaglio.getProduct().getPrice());
        System.out.println("Product_Stock: " + dettaglio.getProduct().getStock());
        System.out.println("Order_Quantity: " + dettaglio.getQuantity());
        System.out.println("Order_Status: " + stato.getOrder_status());
        System.out.println("Payment_Status: " + stato.getPayment_status());
        System.out.println();
    }

    private int id;
    private int user_id;
    private Date date;
    private DettaglioOrdine dettaglio;
    private StatoOrdine stato;
}
