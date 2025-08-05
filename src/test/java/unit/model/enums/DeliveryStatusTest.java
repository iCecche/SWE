package unit.model.enums;

import model.enums.DeliveryStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusTest {

    @Test
    void fromString() {
        DeliveryStatus status = DeliveryStatus.fromString("PENDING");
        assertEquals(DeliveryStatus.PENDING, status);

        status = DeliveryStatus.fromString("SHIPPED");
        assertEquals(DeliveryStatus.SHIPPED, status);

        status = DeliveryStatus.fromString("DELIVERED");
        assertEquals(DeliveryStatus.DELIVERED, status);
    }

    @Test
    void fromStringDefault() {
        assertEquals(DeliveryStatus.PENDING, DeliveryStatus.fromString("some random value"));
    }

    @Test
    void testToString() {
        assertEquals("PENDING", DeliveryStatus.PENDING.toString());
        assertEquals("SHIPPED", DeliveryStatus.SHIPPED.toString());
        assertEquals("DELIVERED", DeliveryStatus.DELIVERED.toString());
    }
}