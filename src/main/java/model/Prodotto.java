package model;

public class Prodotto {

    private int id;
    private String name;
    private String description;
    private int price;
    private int stock;
    private boolean is_deleted;

    public Prodotto(int id, String name, String description, int price, int stock, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.is_deleted = is_deleted;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isDeleted() {
        return is_deleted;
    }

    public void print() {
        System.out.println(id + " " + name + " " + description + " " + price + " " + stock);
    }
}
