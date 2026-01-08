package integration.services;

import model.exceptions.ProductServiceException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import orm.DBManager;
import orm.ProductDAOImplementation;
import services.ProductService;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceIntegrationTest {

    private ProductDAOImplementation productDAO;
    private ProductService productService;
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
                .locations("classpath:orm/migration") // dove tieni gli script SQL
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
        productService = new ProductService(productDAO);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void deleteProduct_success() {
        int id = 1;

        assertDoesNotThrow( () -> productService.deleteProduct(id));
        assertNotNull(productService.getProductById(id));
        assertTrue(productService.getProductById(id).isDeleted());
    }

    @Test
    void deleteProduct_notFound() {
        assertThrows(Exception.class, () -> productService.deleteProduct(1000));
    }

    @Test
    void deleteProduct_alreadyDeleted() {
        // delete product id = 0 to simulate an already deleted product
        productService.deleteProduct(1);

        // try to delete it again -> should throw an exception
        assertThrows(ProductServiceException.class, () -> productService.deleteProduct(1));
    }
}
