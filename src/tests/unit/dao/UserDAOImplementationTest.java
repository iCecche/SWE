package tests.unit.dao;

import main.db.DBManager;
import main.db.QueryResult;
import main.db.UserDAOImplementation;
import main.model.User;
import main.model.UserRole;
import main.rowmapper.UserMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserDAOImplementationTest {

    @Mock
    private DBManager dbManager;
    private UserDAOImplementation userDAO;
    private MockedStatic<DBManager> mockedStatic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedStatic = mockStatic(DBManager.class);
        mockedStatic.when(DBManager::getInstance).thenReturn(dbManager);

        userDAO = new UserDAOImplementation();
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void searchAll() {
        User user1 = new User(1, "username", "password", UserRole.USER, "nome", "cognome", "indirizzo", "cap", "provincia", "stato");
        User user2 = new User(2, "username2", "password2", UserRole.ADMIN, "nome2", "cognome2", "indirizzo2", "cap2", "provincia2", "stato2");
        Object[] params = new Object[0];

        // Configura il comportamento del mock
        QueryResult<User> expectedResult = QueryResult.ofSelect(List.of(user1, user2));
        when(dbManager.execute_query(anyString(), any(UserMapper.class), eq(params))).thenReturn(expectedResult);

        // Esegui il test
        List<User> users = userDAO.searchAll();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(List.of(user1, user2), users);

        verify(dbManager, times(1)).execute_query(anyString(), any(UserMapper.class), eq(params));
        verify(dbManager).execute_query(contains("SELECT"),  any(UserMapper.class), eq(params));
    }

    @Test
    void searchById() {
        User user1 = new User(1, "username", "password", UserRole.USER, "nome", "cognome", "indirizzo", "cap", "provincia", "stato");

        // Configura comportamento del mock
        QueryResult<User> expectedResult = QueryResult.ofSelect(List.of(user1));
        when(dbManager.execute_query(anyString(), any(UserMapper.class), eq(1))).thenReturn(expectedResult);

        // Esegui il test
        User user = userDAO.searchById(1);

        assertNotNull(user);
        assertEquals(user1, user);

        verify(dbManager, times(1)).execute_query(anyString(), any(UserMapper.class), eq(1));
        verify(dbManager).execute_query(contains("WHERE id = ?"),  any(UserMapper.class), eq(1));
    }

    @Test
    void searchByUsername() {
        User user1 = new User(1, "username", "password", UserRole.USER, "nome", "cognome", "indirizzo", "cap", "provincia", "stato");

        // Configura il comportamento del mock
        QueryResult<User> expectedResult = QueryResult.ofSelect(List.of(user1));
        when(dbManager.execute_query(anyString(), any(UserMapper.class), eq("username"))).thenReturn(expectedResult);

        // Esegui il test
        User user = userDAO.searchByUsername("username");

        assertNotNull(user);
        assertEquals(user1, user);

        verify(dbManager, times(1)).execute_query(anyString(), any(UserMapper.class), eq("username"));
        verify(dbManager).execute_query(contains("WHERE username = ?"),  any(UserMapper.class), eq("username"));
    }

    @Test
    void searchUsersInfo() {
        User user1 = new User(1, "username", "password", UserRole.USER, "nome", "cognome", "indirizzo", "cap", "provincia", "stato");
        User user2 = new User(2, "username2", "password2", UserRole.ADMIN, "nome2", "cognome2", "indirizzo2", "cap2", "provincia2", "stato2");
        Object[] params = new Object[0];

        // Configura comportamento del mock
        QueryResult<User> expectedResult = QueryResult.ofSelect(List.of(user1, user2));
        when(dbManager.execute_query(anyString(), any(UserMapper.class), eq(params))).thenReturn(expectedResult);

        // Esegui il test
        List<User> users = userDAO.searchUsersInfo();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(List.of(user1, user2), users);

        verify(dbManager, times(1)).execute_query(anyString(), any(UserMapper.class), eq(params));
        verify(dbManager).execute_query(contains("LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID"),  any(UserMapper.class), eq(params));
    }

    @Test
    void searchUserInfoById() {
        User user1 = new User(1, "username", "password", UserRole.USER, "nome", "cognome", "indirizzo", "cap", "provincia", "stato");

        // Configura comportamento del mock
        QueryResult<User> expectedResult = QueryResult.ofSelect(List.of(user1));
        when(dbManager.execute_query(anyString(), any(UserMapper.class), eq(1))).thenReturn(expectedResult);

        // Esegui il test
        User user = userDAO.searchUserInfoById(1);

        assertNotNull(user);
        assertEquals(user1, user);

        verify(dbManager, times(1)).execute_query(anyString(), any(UserMapper.class), eq(1));
        verify(dbManager).execute_query(contains("LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID WHERE USERS.id = ?"),  any(UserMapper.class), eq(1));
    }
}