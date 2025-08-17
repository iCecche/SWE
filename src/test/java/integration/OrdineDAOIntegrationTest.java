package integration;

import db.DBManager;
import db.OrdineDAOImplementation;
import model.DettaglioOrdine;
import model.Ordine;
import model.OrdineBuilder;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.util.List;


public class OrdineDAOIntegrationTest {

    private static OrdineDAOImplementation ordineDAO;
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
        ordineDAO = new OrdineDAOImplementation(db);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void searchAll() {
        List<Ordine> orders = ordineDAO.searchAll();

        assertEquals(4, orders.size());
    }

    @Test
    void searchByUserID() {
        List<Ordine> orders = ordineDAO.searchByUserID(1);

        assertEquals(1, orders.size());
        assertEquals(1, orders.getFirst().getUser_id());
    }

    @Test
    void searchByUserID_NotFound() {
        List<Ordine> orders = ordineDAO.searchByUserID(10);

        assertTrue(orders.isEmpty());
    }

    @Test
    void insertNewOrder() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withUserId(1);

        Timestamp date = new Timestamp(System.currentTimeMillis());
        builder.withDate(date);

        builder.withPaymentStatus(PaymentStatus.PENDING);
        builder.withDeliveryStatus(DeliveryStatus.PENDING);

        DettaglioOrdine details = new DettaglioOrdine();
        details.setProduct_id(1);
        details.setQuantity(1);

        builder.withDetails(details);

        Ordine ordine = builder.build();
        int order_id = Math.toIntExact(ordineDAO.insertNewOrder(ordine));
        ordine.setOrder_id(order_id);

        List<Ordine> orders = ordineDAO.searchByUserID(1);
        assertEquals(2, orders.size());
        assertTrue(orders.contains(ordine));

        Ordine actualOrder = orders.stream().filter(o -> o.getOrder_id() == order_id).findFirst().orElse(null);
        assertNotNull(actualOrder);

        assertEquals(1, actualOrder.getDetails().size());
        assertEquals(1, actualOrder.getDetails().getFirst().getProduct_id());
        assertEquals(1, actualOrder.getDetails().getFirst().getQuantity());

        assertEquals(PaymentStatus.PENDING, actualOrder.getPaymentStatus());
        assertEquals(DeliveryStatus.PENDING, actualOrder.getDeliveryStatus());
    }

    @Test
    void insertNewOrder_MultipleItems() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withUserId(5);

        Timestamp date = new Timestamp(System.currentTimeMillis());
        builder.withDate(date);

        builder.withPaymentStatus(PaymentStatus.PENDING);
        builder.withDeliveryStatus(DeliveryStatus.PENDING);

        DettaglioOrdine details = new DettaglioOrdine();
        details.setProduct_id(1);
        details.setQuantity(1);

        DettaglioOrdine details2 = new DettaglioOrdine();
        details2.setProduct_id(2);
        details2.setQuantity(2);

        builder.withDetails(details);
        builder.withDetails(details2);

        Ordine ordine = builder.build();
        int order_id = Math.toIntExact(ordineDAO.insertNewOrder(ordine));
        ordine.setOrder_id(order_id);

        List<Ordine> orders = ordineDAO.searchByUserID(5);
        assertEquals(1, orders.size());
        assertTrue(orders.contains(ordine));

        Ordine actualOrder = orders.stream().filter(o -> o.getOrder_id() == order_id).findFirst().orElse(null);
        assertNotNull(actualOrder);

        assertEquals(2, actualOrder.getDetails().size());
        assertEquals(1, actualOrder.getDetails().getFirst().getProduct_id());
        assertEquals(1, actualOrder.getDetails().getFirst().getQuantity());
        assertEquals(2, actualOrder.getDetails().getLast().getProduct_id());
        assertEquals(2, actualOrder.getDetails().getLast().getQuantity());

        assertEquals(PaymentStatus.PENDING, actualOrder.getPaymentStatus());
        assertEquals(DeliveryStatus.PENDING, actualOrder.getDeliveryStatus());
    }

    @Test
    void updateDeliveryStatus () {
        DeliveryStatus status = DeliveryStatus.SHIPPED;
        int order_id = 1;

        ordineDAO.updateDeliveryStatus(order_id, status);

        Ordine ordine = ordineDAO.searchByUserID(1).getFirst();
        assertEquals(status, ordine.getDeliveryStatus());
    }

    @Test
    void updatePaymentStatus () {
        PaymentStatus status = PaymentStatus.PAID;
        int order_id = 1;

        ordineDAO.updatePaymentStatus(order_id, status);

        Ordine ordine = ordineDAO.searchByUserID(1).getFirst();
        assertEquals(status, ordine.getPaymentStatus());
    }

    @Test
    void deleteOrder () {
        int order_id = 1;
        ordineDAO.deleteOrder(order_id);

        List<Ordine> orders = ordineDAO.searchByUserID(1);
        assertFalse(orders.stream().anyMatch(o -> o.getOrder_id() == order_id));
    }
}
