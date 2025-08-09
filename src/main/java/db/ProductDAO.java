package db;

import model.DettaglioOrdine;
import model.Prodotto;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {

    // Search
    List<Prodotto> searchAll();
    Prodotto searchById(int id);

    // Insert
    void insertNewProduct(String name, String description, Integer price, Integer stock_quantity);

    // Update
    void updateProduct(int id, String name, String description, Integer price, Integer stock);

    // Delete
    void deleteProduct(int id);
}
