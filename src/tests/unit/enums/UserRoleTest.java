package tests.unit.enums;

import main.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void fromString() {
        UserRole role = UserRole.fromString("USER");
        assertNotNull(role);
        assertEquals(UserRole.USER, role);

        role = UserRole.fromString("ADMIN");
        assertNotNull(role);
        assertEquals(UserRole.ADMIN, role);
    }

    @Test
    void fromStringDefault() {
        assertEquals(UserRole.USER, UserRole.fromString("some random value"));
    }

    @Test
    void testToString() {
        assertEquals("USER", UserRole.USER.toString());
        assertEquals("ADMIN", UserRole.ADMIN.toString());
    }
}