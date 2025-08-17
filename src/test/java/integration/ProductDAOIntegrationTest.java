package integration;

import db.DBManager;
import db.ProductDAOImplementation;
import model.Prodotto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOIntegrationTest {
    private static ProductDAOImplementation productDAO;
    private static PostgreSQLContainer<?> postgres;
    private static Flyway flyway;

    @BeforeAll
    static void initDB() {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("test_db")
                .withUsername("user_test")
                .withPassword("password_test");

        postgres.start();

        flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration") // dove tieni gli script SQL
                .cleanDisabled(false)
                .load();
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();

        final String jdbcURL = postgres.getJdbcUrl();
        final String jdbcUsername = postgres.getUsername();
        final String jdbcPassword = postgres.getPassword();
        final String jdbcDriver = "org.postgresql.Driver";

        DBManager db = DBManager.getTestInstance(jdbcURL, jdbcUsername, jdbcPassword, jdbcDriver);
        productDAO = new ProductDAOImplementation(db);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
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
        productDAO.deleteProduct(10);

        Prodotto product = productDAO.searchById(10);
        assertTrue(product.isDeleted());
    }
}