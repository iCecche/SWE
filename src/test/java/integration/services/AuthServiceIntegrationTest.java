package integration.services;

import model.User;
import model.enums.UserRole;
import model.exceptions.AuthServiceException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import orm.DBManager;
import orm.UserDAOImplementation;
import services.AuthService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceIntegrationTest {
    private UserDAOImplementation userDAO;
    private AuthService authService;
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
        authService = new AuthService(userDAO);
    }

    @AfterAll
    static void tearDown() {
        postgres.close();
    }

    @Test
    void login_success() {
        String username = "admin";
        String password = "admin";

        User user = assertDoesNotThrow( () -> authService.login(username, password));
        assertInstanceOf(User.class, user);
    }

    @Test
    void login_notFound() {
        String username = "username";
        String password = "password";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.login(username, password));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void login_wrongPassword() {
        String username = "admin";
        String password = "";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.login(username, password));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void login_emptyCredentials() {
        String username = "";
        String password = "";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.login(username, password));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void login_deletedUser() {
        String username = "user9";
        String password = "user9";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.login(username, password));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void register_success() {
        String username = "username";
        String password = "password";
        String confirmPassword = "password";
        String nome = "nome";
        String cognome = "cognome";
        UserRole role = UserRole.USER;

        assertDoesNotThrow(() -> authService.register(username, password, confirmPassword, role, nome, cognome));
        assertNotNull(userDAO.searchByUsername("username"));
    }

    @Test
    void register_alreadyExists() {
        String username = "admin";
        String password = "admin";
        String confirmPassword = "admin";
        String nome = "nome";
        String cognome = "cognome";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.register(username, password, confirmPassword, UserRole.USER, nome, cognome));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void register_emptyFields() {
        String username = "";
        String password = "";
        String confirmPassword = "";
        String nome = "";
        String cognome = "";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.register(username, password, confirmPassword, UserRole.USER, nome, cognome));
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    void register_wrongPassword() {
        String username = "username";
        String password = "password";
        String confirmPassword = "pass";
        String nome = "nome";
        String cognome = "cognome";

        Throwable exception = assertThrows(AuthServiceException.class, () -> authService.register(username, password, confirmPassword, UserRole.USER, nome, cognome));
        assertFalse(exception.getMessage().isEmpty());
    }
}
