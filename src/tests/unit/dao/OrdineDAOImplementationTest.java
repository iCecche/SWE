package tests.unit.dao;

import main.db.DBManager;
import main.db.OrdineDAOImplementation;
import main.db.QueryResult;
import main.model.*;
import main.rowmapper.OrdineMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrdineDAOImplementationTest {

    @Mock
    private DBManager dbManager;
    private OrdineDAOImplementation ordineDAO;
    private MockedStatic<DBManager> mockedStatic;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        mockedStatic = mockStatic(DBManager.class);
        mockedStatic.when(DBManager::getInstance).thenReturn(dbManager);

        ordineDAO = new OrdineDAOImplementation(); // ora il mock è ancora attivo
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close(); // chiudi manualmente
    }


    @Test
    void searchAll() {
        // Prepara i dati di test
        Ordine ordine1 = new Ordine(1, 1, new Date(), new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Ordine ordine2 = new Ordine(2, 2, new Date(), new ArrayList<>(), PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Object[] params = new Object[0];

        // Configura il comportamento del mock
        QueryResult<Ordine> expectedOrders = QueryResult.ofSelect(List.of(ordine1, ordine2));
        when(dbManager.execute_query(anyString(), any(OrdineMapper.class), eq(params))).thenReturn(expectedOrders);

        // Esegui il test
        List<Ordine> actualOrders = ordineDAO.searchAll();

        assertNotNull(actualOrders);
        verify(dbManager).execute_query(contains("SELECT"), any(OrdineMapper.class));
    }


    @Test
    void searchByUserID() {
        Ordine ordine1 = new Ordine(1, 2, null, null, PaymentStatus.PENDING, DeliveryStatus.PENDING);
        Ordine ordine2 = new Ordine(2, 2, null, null, PaymentStatus.PENDING, DeliveryStatus.PENDING);

        // Configura il comportamento del mock
        QueryResult<Ordine> expectedOrders = QueryResult.ofSelect(List.of(ordine1, ordine2));
        when(dbManager.execute_query(anyString(), any(OrdineMapper.class), any())).thenReturn(expectedOrders);

        // Esegui il test
        List<Ordine> actualOrders = ordineDAO.searchByUserID(2);

        assertNotNull(actualOrders);
        for (Ordine order : actualOrders) { assertEquals(2, order.getUser_id());}
        verify(dbManager).execute_query(contains("WHERE user_id = ?"), any(OrdineMapper.class), eq(2));
    }
}