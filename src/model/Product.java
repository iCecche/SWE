package model;

public class Product {

    private int id;
    private String name;
    private String description;
    private int price;
    private int stock;

    public Product() {};
    public Product(int id, String name, String description, int price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
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

    public void print() {
        System.out.println(id + " " + name + " " + description + " " + price + " " + stock);
    }
}
