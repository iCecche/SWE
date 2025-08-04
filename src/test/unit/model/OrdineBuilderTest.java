package test.unit.model;

import main.model.enums.DeliveryStatus;
import main.model.enums.PaymentStatus;
import main.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdineBuilderTest {

    private OrdineBuilder builder;

    @BeforeEach
    void setUp() {
        builder = OrdineBuilder.create();
    }

    @Test
    void create() {
        assertNotNull(builder);
        assertEquals(OrdineBuilder.class, builder.getClass());
    }

    @Test
    void withOrderId() {
        builder.withOrderId(1);

        assertEquals(1, builder.build().getOrder_id());
    }

    @Test
    void withUserId() {
        builder.withUserId(1);

        assertEquals(1, builder.build().getUser_id());
    }

    @Test
    void withDate() {
        Date date = new Date();
        builder.withDate(date);

        assertEquals(date, builder.build().getDate());
    }

    @Test
    void withDetails() {
        DettaglioOrdine detail = new DettaglioOrdine(1, 1);
        builder.withDetails(detail);

        assertEquals(detail, builder.build().getDetails().getFirst());
        assertEquals(1, builder.build().getDetails().size());
    }

    @Test
    void testWithDetails() {
        List<DettaglioOrdine> details = List.of(new DettaglioOrdine(1, 1), new DettaglioOrdine(2, 2));
        builder.withDetails(details);

        assertEquals(2, builder.build().getDetails().size());
    }

    @Test
    void withPaymentStatus() {
        builder.withPaymentStatus(PaymentStatus.PENDING);

        assertEquals(PaymentStatus.PENDING, builder.build().getPaymentStatus());
    }

    @Test
    void withDeliveryStatus() {
        builder.withDeliveryStatus(DeliveryStatus.PENDING);

        assertEquals(DeliveryStatus.PENDING, builder.build().getDeliveryStatus());
    }

    @Test
    void build() {
        Ordine createdOrder = builder.build();

        assertEquals(Ordine.class, createdOrder.getClass());
        assertNotNull(createdOrder);
    }
}