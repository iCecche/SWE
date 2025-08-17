package integration;

import db.DBManager;
import db.UserDAOImplementation;
import model.User;
import model.UserBuilder;
import model.enums.UserRole;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOIntegrationTest {

    private UserDAOImplementation userDAO;
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
        userDAO = new UserDAOImplementation(db);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void searchAll() {
        List<User> users = userDAO.searchAll();

        assertNotNull(users);
        assertEquals(10, users.size());
    }

    @Test
    void searchById() {
        User user = userDAO.searchById(10);

        assertNotNull(user);
        assertEquals(10, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());

    }

    @Test
    void searchById_notFound() {
        User user = userDAO.searchById(1000);

        assertNull(user);
    }

    @Test
    void searchByUsername() {
        User user = userDAO.searchByUsername("admin");

        assertNotNull(user);
        assertEquals(10, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void searchByUsername_notFound() {
        User user = userDAO.searchByUsername("notFound");

        assertNull(user);
    }

    @Test
    void searchByUsername_caseInsensitive() {
        User user = userDAO.searchByUsername("ADMIN");

        assertNotNull(user);
        assertEquals(10, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void searchUserInfo() {
        List<User> users_with_info = userDAO.searchUsersInfo();

        for (User user : users_with_info) {
            assertNotNull(user.getNome());
            assertNotNull(user.getCognome());
            assertNotNull(user.getIndirizzo());
            assertNotNull(user.getCap());
            assertNotNull(user.getProvincia());
            assertNotNull(user.getStato());
        }
    }

    @Test
    void searchUserInfo_byId() {
        User user = userDAO.searchUserInfoById(10);

        assertNotNull(user.getNome());
        assertNotNull(user.getCognome());
        assertNotNull(user.getIndirizzo());
        assertNotNull(user.getCap());
        assertNotNull(user.getProvincia());
        assertNotNull(user.getStato());
    }

    @Test
    void searchUserInfo_byId_notFound() {
        User user = userDAO.searchUserInfoById(1000);
        assertNull(user);
    }

    @Test
    void insertNewUser() {
        UserBuilder builder = UserBuilder.create();
        builder.withUsername("test_username");
        builder.withPassword("");
        builder.withRole(UserRole.USER);
        builder.withNome("test_nome");
        builder.withCognome("test_cognome");

        User new_user = builder.build();
        int new_user_id = Math.toIntExact(userDAO.newUser(new_user.getUsername(), new_user.getPassword(), new_user.getRole(), new_user.getNome(), new_user.getCognome()));

        builder.withId(new_user_id);
        new_user = builder.build();

        List<User> users = userDAO.searchAll();

        assertEquals(new_user, userDAO.searchById(new_user_id));
        assertTrue(users.contains(new_user));
    }

    @Test
    void insertNewUser_alreadyExists() {
        assertThrows(Exception.class, () -> userDAO.newUser("admin", "admin", UserRole.ADMIN, "a_name", "a_surname"));
    }

    @Test
    void updateRole() {
        userDAO.UpdateRole(10, UserRole.USER);
        User user = userDAO.searchById(10);

        assertNotNull(user);
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void updateRole_notFound() {
        assertThrows(Exception.class, () -> userDAO.UpdateRole(1000, UserRole.USER));
    }

    @Test
    void updateCredentials() {
        userDAO.UpdateCredentials(10, "new_username", "new_password");

        User user = userDAO.searchById(10);
        assertNotNull(user);
        assertEquals("new_username", user.getUsername());
        assertEquals("new_password", user.getPassword());
    }

    @Test
    void updateCredentials_notFound() {
        assertThrows(Exception.class, () -> userDAO.UpdateCredentials(1000, "new_username", "new_password"));
    }

    @Test
    void updateUserInfo() {
        userDAO.UpdateUserInfo(10, "new_nome", "new_cognome", "new_indirizzo", "new_cap", "np", "ns");

        User user = userDAO.searchUserInfoById(10);
        assertNotNull(user);
        assertEquals("new_nome", user.getNome());
        assertEquals("new_cognome", user.getCognome());
        assertEquals("new_indirizzo", user.getIndirizzo());
        assertEquals("new_cap", user.getCap());
        assertEquals("np", user.getProvincia());
        assertEquals("ns", user.getStato());
    }

    @Test
    void updateUserInfo_notFound() {
        assertThrows(Exception.class, () -> userDAO.UpdateUserInfo(1000, "new_nome", "new_cognome", "new_indirizzo", "new_cap", "np", "ns"));
    }

    @Test
    void deleteUser() {
        userDAO.deleteUser(10);
        User user = userDAO.searchById(10);

        assertTrue(user.isDeleted());
    }

    @Test
    void deleteUser_notFound() {
        assertThrows(Exception.class, () -> userDAO.deleteUser(1000));
    }

}
