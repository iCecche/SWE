package unit.services;

import model.User;
import model.enums.UserRole;
import model.exceptions.AuthServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import orm.UserDAOImplementation;
import services.AuthService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    @Mock
    private UserDAOImplementation userDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        authService = new AuthService(userDAO);
    }

    @Test
    void login_success() {
        String username = "username";
        String password = "password";

        // Crea l'utente che il mock deve restituire
        User expectedUser = new User(1, username, password, UserRole.USER, false);
        when(userDAO.searchByUsername(username)).thenReturn(expectedUser);

        // Esegui il test
        User user = authService.login(username, password);

        // Verifica
        assertEquals(expectedUser, user);
        assertDoesNotThrow(() -> authService.login(username, password));
    }

    @Test
    void login_notFound() {
        String username = "username";
        String password = "password";

        when(userDAO.searchByUsername(anyString())).thenReturn(null);

        assertThrows(AuthServiceException.class, () -> authService.login(username, password));
    }

    @Test
    void login_wrongPassword() {
        String username = "username";
        String password = "password";

        User expectedUser = new User(1, username, "wrong_password", UserRole.USER, false);
        when(userDAO.searchByUsername(anyString())).thenReturn(expectedUser);

        assertThrows(AuthServiceException.class, () -> authService.login(username, password));
    }

    @Test
    void register_success() {
        String username = "username";
        String password = "password";
        String nome = "nome";
        String cognome = "cognome";

        String indirizzo = "indirizzo";
        String cap = "cap";
        String provincia = "provincia";
        String stato = "stato";
        UserRole role = UserRole.USER;

        // Crea l'utente che il mock deve restituire'
        User expectedUser = new User(1, username, password, role, nome, cognome, indirizzo, cap, provincia, stato, false);
        when(userDAO.searchByUsername(username)).thenReturn(null);
        when(userDAO.newUser(username, password, role, nome, cognome)).thenReturn(1L);

        assertDoesNotThrow(() -> authService.register(username, password, password, role, nome, cognome));
        assertDoesNotThrow(() -> authService.register(username, password, password, role, nome, cognome));
    }

    @Test
    void register_alreadyExists() {
        String username = "username";
        String password = "password";
        String nome = "nome";
        String cognome = "cognome";

        String indirizzo = "indirizzo";
        String cap = "cap";
        String provincia = "provincia";
        String stato = "stato";
        UserRole role = UserRole.USER;

        User expectedUser = new User(1, username, password, role, nome, cognome, indirizzo, cap, provincia, stato, false);
        when(userDAO.searchByUsername(username)).thenReturn(expectedUser);

        assertThrows(AuthServiceException.class, () -> authService.register(username, password, password, role, nome, cognome));
    }

    @Test
    void register_ormError() {
        String username = "username";
        String password = "password";
        String nome = "nome";
        String cognome = "cognome";
        UserRole role = UserRole.USER;

        when(userDAO.newUser(username, password, role, nome, cognome)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> authService.register(username, password, password, role, nome, cognome));
    }
}
