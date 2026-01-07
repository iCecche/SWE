package services;

import model.Prodotto;
import model.exceptions.ProductServiceException;
import orm.ProductDAOImplementation;

import java.util.List;

public class ProductService {

    private final ProductDAOImplementation productDAO;

    // Constructor needed for testing
    public ProductService(ProductDAOImplementation productDAO) {
        this.productDAO = productDAO;
    }

    public ProductService() {
        productDAO = new ProductDAOImplementation();
    }

    public List<Prodotto> getAllProducts() {
        return productDAO.searchAll();
    }

    public Prodotto getProductById(int id) {
        return productDAO.searchById(id);
    }

    public void insertNewProduct(String name, String description, Integer price, Integer stock_quantity) {
        productDAO.insertNewProduct(name, description, price, stock_quantity);
    }

    public void updateProduct(int id, String name, String description, Integer price, Integer stock) {
        productDAO.updateProduct(id, name, description, price, stock);
    }

    public void deleteProduct(int id) {
        Prodotto product = productDAO.searchById(id);

        // Controllo se il prodotto è già stato eliminato
        if (product.isDeleted()) {
            throw new ProductServiceException("Il prodotto è già eliminato");
        }

        // Se tutti i controlli passano, procedo
        productDAO.deleteProduct(id);
    }
}
