package integration.services;

import model.User;
import model.enums.UserRole;
import model.exceptions.UserServiceException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import orm.DBManager;
import orm.UserDAOImplementation;
import services.SessionManager;
import services.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest {

    private UserService userService;
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
        userDAO = new UserDAOImplementation(db);
        userService = new UserService(userDAO);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void updateUserRole_success() {
        int userId = 1;
        User user = userService.getUserById(userId);

        assertTrue(user.getRole() == UserRole.USER);
        assertDoesNotThrow(() -> userService.updateRole(userId));

        User updatedUser = userService.getUserById(userId);
        assertTrue(updatedUser.getRole() == UserRole.ADMIN);
    }

    @Test
    void updateUserRole_deletedUser() {
        String username = "user9";
        User user = userService.getUserByUsername(username);

        assertTrue(user.isDeleted());
        Throwable ex = assertThrows(UserServiceException.class, () -> userService.updateRole(user.getId()));
        assertFalse(ex.getMessage().isEmpty());

        User updatedUser = userService.getUserByUsername(username);
        assertTrue(updatedUser.isDeleted());
        assertEquals(UserRole.USER, updatedUser.getRole());
    }

    @Test
    void updateUserInfo_success() {
        String username = "user1";
        String nome = "new_nome";
        String cognome = "new_cognome";
        String indirizzo = "new_indirizzo";
        String cap = "new_cap";
        String provincia = "np";
        String stato = "ns";


        User user = userService.getUserByUsername(username);
        int userId = user.getId();

        assertDoesNotThrow(() -> userService.updateUserInfo(userId, "new_nome", "new_cognome", "new_indirizzo", "new_cap", "np", "ns"));
        userDAO.UpdateUserInfo(userId, nome, cognome, indirizzo, cap, provincia, stato);

        User updatedUser = userService.getUserInfoById(userId);
        assertEquals(nome, updatedUser.getNome());
        assertEquals(cognome, updatedUser.getCognome());
        assertEquals(indirizzo, updatedUser.getIndirizzo());
        assertEquals(cap, updatedUser.getCap());
        assertEquals(provincia, updatedUser.getProvincia());
        assertEquals(stato, updatedUser.getStato());
    }

    @Test
    void updateUserInfo_deletedUser() {
        String username = "user9";
        User user = userService.getUserByUsername(username);

        assertTrue(user.isDeleted());
        Throwable ex = assertThrows(UserServiceException.class, () -> userService.updateUserInfo(user.getId(), "nome", "cognome", "indirizzo", "cap", "provincia", "stato"));
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void deleteUser_success() {
        SessionManager manager = SessionManager.getInstance();

        int userId = 1;
        User user = userService.getUserById(userId);
        manager.login(user);

        assertFalse(user.isDeleted());
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        User deletedUser = userService.getUserById(userId);
        assertTrue(deletedUser.isDeleted());
    }

    @Test
    void deleteUser_alreadyDeleted() {
        String username = "user9";
        User user = userService.getUserByUsername(username);
        int userId = user.getId();

        assertTrue(user.isDeleted());
        Throwable ex = assertThrows(UserServiceException.class, () -> userService.deleteUser(userId));
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void deleteUser_adminSelfDelete() {
        SessionManager manager = SessionManager.getInstance();
        String username = "admin";
        User user = userService.getUserByUsername(username);
        int userId = user.getId();
        manager.login(user);

        assertTrue(user.getRole() == UserRole.ADMIN);
        assertFalse(user.isDeleted());

        Throwable ex = assertThrows(UserServiceException.class, () -> userService.deleteUser(userId));
        assertFalse(ex.getMessage().isEmpty());
    }

}
