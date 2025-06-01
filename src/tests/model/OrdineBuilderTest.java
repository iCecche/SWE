package tests.model;

import main.model.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdineBuilderTest {

    @Test
    void create() {
        OrdineBuilder builder = OrdineBuilder.create();
        assertNotNull(builder);
        assertEquals(OrdineBuilder.class, builder.getClass());
    }

    @Test
    void withOrderId() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withOrderId(1);

        assertEquals(1, builder.build().getOrder_id());
    }

    @Test
    void withUserId() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withUserId(1);

        assertEquals(1, builder.build().getUser_id());
    }

    @Test
    void withDate() {
        Date date = new Date();
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withDate(date);

        assertEquals(date, builder.build().getDate());
    }

    @Test
    void withDetails() {
        DettaglioOrdine detail = new DettaglioOrdine(1, 1);
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withDetails(detail);

        assertEquals(detail, builder.build().getDetails().getFirst());
        assertEquals(1, builder.build().getDetails().size());
    }

    @Test
    void testWithDetails() {
        List<DettaglioOrdine> details = List.of(new DettaglioOrdine(1, 1), new DettaglioOrdine(2, 2));
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withDetails(details);

        assertEquals(2, builder.build().getDetails().size());
    }

    @Test
    void withPaymentStatus() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withPaymentStatus(PaymentStatus.PENDING);

        assertEquals(PaymentStatus.PENDING, builder.build().getPaymentStatus());
    }

    @Test
    void withDeliveryStatus() {
        OrdineBuilder builder = OrdineBuilder.create();
        builder.withDeliveryStatus(DeliveryStatus.PENDING);

        assertEquals(DeliveryStatus.PENDING, builder.build().getDeliveryStatus());
    }

    @Test
    void build() {
        OrdineBuilder builder = OrdineBuilder.create();

        assertEquals(Ordine.class, builder.build().getClass());
    }
}