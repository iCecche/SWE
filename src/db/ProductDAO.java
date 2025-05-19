package db;

import model.Product;
import java.util.List;

public interface ProductDAO {

    abstract List<Product> search();

    abstract void insert(String name, String description, Integer price, Integer stock_quantity);

    abstract void update(int id, String name, String description, Integer price, Integer stock);

    abstract void delete(int id);
}
