package integration.services;

import model.Ordine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import model.exceptions.OrderBusinessException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import orm.DBManager;
import orm.OrdineDAOImplementation;
import services.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceIntegrationTest {
    private OrderService orderService;
    private OrdineDAOImplementation ordineDAO;
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
        ordineDAO = new OrdineDAOImplementation(db);
        orderService = new OrderService(ordineDAO);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void shipOrder_success() {
        // User 2 in the test_data has paid the order and the order is not shipped yet
        // User 2 has only a single order
        int userId = 2;
        Ordine ordine = orderService.getOrdersByUserID(userId).getFirst();

        assertEquals(DeliveryStatus.PENDING, ordine.getDeliveryStatus());
        assertDoesNotThrow(() -> orderService.shipOrder(ordine.getOrder_id()));

        Ordine updatedOrder = ordineDAO.searchByUserID(userId).getFirst();
        assertEquals(DeliveryStatus.SHIPPED, updatedOrder.getDeliveryStatus());
    }

    @Test
    void shipOrder_notPaid() {
        // User 1 in the test_data has not paid the order yet
        // User 1 has only a single order
        int userId = 1;
        Ordine ordine = orderService.getOrdersByUserID(userId).getFirst();

        Throwable exception = assertThrows(OrderBusinessException.class, () -> orderService.shipOrder(ordine.getOrder_id()));
        assertFalse(exception.getMessage().isEmpty());

        Ordine updatedOrder = ordineDAO.searchByUserID(userId).getFirst();
        assertEquals(DeliveryStatus.PENDING, updatedOrder.getDeliveryStatus());
    }

    @Test
    void shipOrder_alreadyShipped(){
        // User 3 in the test_data has already shipped the order
        // User 3 has only a single order
        int userId = 3;
        Ordine ordine = orderService.getOrdersByUserID(userId).getFirst();

        assertNotEquals(DeliveryStatus.PENDING, ordine.getDeliveryStatus());
        Throwable exception = assertThrows(OrderBusinessException.class, () -> orderService.shipOrder(ordine.getOrder_id()));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void payOrder_success() {
        // User 1 in the test_data has not paid the order yet
        // User 1 has only a single order
        int userId = 1;
        Ordine ordine = orderService.getOrdersByUserID(userId).getFirst();

        assertNotEquals(PaymentStatus.PAID, ordine.getPaymentStatus());
        assertDoesNotThrow(() -> orderService.payOrder(ordine.getOrder_id()));

        Ordine updatedOrder = ordineDAO.searchByUserID(userId).getFirst();
        assertEquals(PaymentStatus.PAID, updatedOrder.getPaymentStatus());
    }

    @Test
    void payOrder_alreadyPaid() {
        // User 2 in the test_data has already paid the order
        // User 2 has only a single order
        int userId = 2;
        Ordine ordine = orderService.getOrdersByUserID(userId).getFirst();

        assertEquals(PaymentStatus.PAID, ordine.getPaymentStatus());
        Throwable exception = assertThrows(OrderBusinessException.class, () -> orderService.payOrder(ordine.getOrder_id()));
        assertFalse(exception.getMessage().isEmpty());
    }
}
