package unit.model.enums;

import model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void fromString() {
        PaymentStatus status = PaymentStatus.fromString("PENDING");
        assertNotNull(status);
        assertEquals(PaymentStatus.PENDING, status);

        status = PaymentStatus.fromString("PAID");
        assertNotNull(status);
        assertEquals(PaymentStatus.PAID, status);

        status = PaymentStatus.fromString("FAILED");
        assertNotNull(status);
        assertEquals(PaymentStatus.FAILED, status);
    }

    @Test
    void fromStringDefault() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("some random value"));
    }

    @Test
    void testToString() {
        assertEquals("PENDING", PaymentStatus.PENDING.toString());
        assertEquals("PAID", PaymentStatus.PAID.toString());
        assertEquals("FAILED", PaymentStatus.FAILED.toString());
    }
}