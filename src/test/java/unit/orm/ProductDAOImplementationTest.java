package unit.orm;

import orm.DBManager;
import orm.ProductDAOImplementation;
import orm.QueryResult;
import model.Prodotto;

import orm.rowmapper.ProductMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductDAOImplementationTest {

    @Mock
    private DBManager dbManager;
    private ProductDAOImplementation productDAO;
    private MockedStatic<DBManager> mockedStatic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedStatic = mockStatic(DBManager.class);
        mockedStatic.when(DBManager::getInstance).thenReturn(dbManager);

        productDAO = new ProductDAOImplementation();
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void searchAll() {
        Prodotto prodotto = new Prodotto(1, "Nome", "Descrizione", 10, 100, false);
        Object[] params = new Object[0];

        // Configura il comportamento del mock
        QueryResult<Prodotto> expectedResult = QueryResult.ofSelect(List.of(prodotto));
        when(dbManager.execute_query(anyString(), any(ProductMapper.class), eq(params))).thenReturn(expectedResult);

        // Esegui il test
        List<Prodotto> actualResult = productDAO.searchAll();

        assertNotNull(actualResult);
        verify(dbManager).execute_query(contains("SELECT"), any(ProductMapper.class));
        verify(dbManager, times(1)).execute_query(anyString(), any(ProductMapper.class));

    }

    @Test
    void searchById() {
        Prodotto prodotto = new Prodotto(10, "Nome", "Descrizione", 10, 100, false);

        // Configura il comportamento del mock
        QueryResult<Prodotto> expectedResult = QueryResult.ofSelect(List.of(prodotto));
        when(dbManager.execute_query(anyString(), any(ProductMapper.class), anyInt())).thenReturn(expectedResult);

        // Esegui il test
        Prodotto actualResult = productDAO.searchById(10);

        assertNotNull(actualResult);
        assertEquals(prodotto, actualResult);
        verify(dbManager).execute_query(contains("WHERE id = ?"), any(ProductMapper.class), eq(10));
        verify(dbManager, times(1)).execute_query(anyString(), any(ProductMapper.class), anyInt());
    }
}