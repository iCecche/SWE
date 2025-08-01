package tests.integration;

import main.db.DBManager;
import main.db.ProductDAOImplementation;
import main.model.Prodotto;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOIntegrationTest {
    private static ProductDAOImplementation productDAO;
    private static DBManager db;

    @BeforeAll
    static void initDB() {
        final String jdbcURL = "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1";
        final String jdbcUsername = "user_test";
        final String jdbcPassword = "password_test";
        final String jdbcDriver = "org.h2.Driver";

        db = DBManager.getTestInstance(jdbcURL, jdbcUsername, jdbcPassword, jdbcDriver);
    }

    @BeforeEach
    void setUp() {
        // inizializza lo schema del db
        db.executeSqlFile("/Users/gabrielececcherini/IdeaProjects/SWE/src/tests/resources/schema.sql");
        db.executeSqlFile("/Users/gabrielececcherini/IdeaProjects/SWE/src/tests/resources/test-data.sql");

        productDAO = new ProductDAOImplementation(db);
    }

    @AfterEach
    void tearDown() {
        db.executeSqlFile("/Users/gabrielececcherini/IdeaProjects/SWE/src/tests/resources/clean-up.sql");
    }

    @Test
    void searchAll() {
        List<Prodotto> products = productDAO.searchAll();

        assertEquals(10, products.size());
        assertEquals("Laptop", products.getFirst().getName());
    }

    @Test
    void searchById() {
        Prodotto product_1 = productDAO.searchById(1);
        Prodotto product_2 = productDAO.searchById(10);


        assertEquals(1, product_1.getId());
        assertEquals(10, product_2.getId());
        assertNotEquals(product_1, product_2);
    }

    @Test
    void insertNewProduct() {
        productDAO.insertNewProduct("Rayban", "Occhiali Rayban", 1000, 50);

        List<Prodotto> products = productDAO.searchAll();
        Prodotto lastProduct = products.getLast();

        assertEquals(11, products.size());
        assertEquals(11, lastProduct.getId());
        assertEquals("Rayban", lastProduct.getName());
        assertEquals(1000, lastProduct.getPrice());
        assertEquals(50, lastProduct.getStock());
    }

    @Test
    void updateProduct() {
        productDAO.updateProduct(10, "NewName", "NewDescription", 100, 10);
        Prodotto product = productDAO.searchById(10);

        assertNotNull(product);
        assertEquals("NewName", product.getName());
        assertEquals("NewDescription", product.getDescription());
        assertEquals(100, product.getPrice());
        assertEquals(10, product.getStock());
    }

    @Test
    void deleteProduct() {
        // check products quantity and retrieve product to be deleted
        List<Prodotto> products = productDAO.searchAll();
        Prodotto product = products.getLast();

        assertEquals(10, products.size());
        assertNotNull(product);
        assertEquals(10, product.getId());

        // FIXME: gestire cancellazione prodotto ( se presente in ordini c'è problema ) -> flag su main.db 'is_deleted' invece di cancellazione oppure set null?

        // delete product
        //productDAO.deleteProduct(10);

        // check list to confirm deletion
        //List<Prodotto> remaining_products = productDAO.searchAll();

        //assertEquals(9, remaining_products.size());
        //assertFalse(products.contains(product));
    }
}