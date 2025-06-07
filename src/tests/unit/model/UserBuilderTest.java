package tests.unit.model;

import main.model.User;
import main.model.UserBuilder;
import main.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserBuilderTest {

    private UserBuilder builder;

    @BeforeEach void setUp() {
        builder = UserBuilder.create();
    }

    @Test
    void create() {
        assertNotNull(builder);
        assertEquals(UserBuilder.class, builder.getClass());
    }

    @Test
    void withId() {
        builder.withId(1);

        assertEquals(1, builder.build().getId());
    }

    @Test
    void withUsername() {
        builder.withUsername("username");

        assertEquals("username", builder.build().getUsername());
    }

    @Test
    void withPassword() {
        builder.withPassword("password");

        assertEquals("password", builder.build().getPassword());
    }

    @Test
    void withRole() {
        builder.withRole(UserRole.USER);

        assertNotNull(builder.build().getRole());
        assertEquals(UserRole.USER, builder.build().getRole());
    }

    @Test
    void withNome() {
        builder.withNome("nome");

        assertEquals("nome", builder.build().getNome());
    }

    @Test
    void withCognome() {
        builder.withCognome("cognome");

        assertEquals("cognome", builder.build().getCognome());
    }

    @Test
    void withIndirizzo() {
        builder.withIndirizzo("indirizzo");

        assertEquals("indirizzo", builder.build().getIndirizzo());
    }

    @Test
    void withCap() {
        builder.withCap("50018");

        assertEquals("50018", builder.build().getCap());
    }

    @Test
    void withProvincia() {
        builder.withProvincia("FI");

        assertEquals("FI", builder.build().getProvincia());
    }

    @Test
    void withStato() {
        builder.withStato("Italia");

        assertEquals("Italia", builder.build().getStato());
    }

    @Test
    void build() {
        User createdUser = builder.build();

        assertEquals(User.class, createdUser.getClass());
        assertNotNull(createdUser);
    }
}