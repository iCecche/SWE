package test.unit.model;

import main.model.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProdottoTest {

    private Prodotto prodotto;

    @BeforeEach
    void setUp() {
        prodotto = new Prodotto(1, "nome", "descrizione", 1, 1);
    }

    @Test
    void getId() {
        int id = prodotto.getId();
        assertEquals(1, id);
    }

    @Test
    void getName() {
        String name = prodotto.getName();
        assertEquals("nome", name);
    }

    @Test
    void getDescription() {
        String description = prodotto.getDescription();
        assertEquals("descrizione", description);
    }

    @Test
    void getPrice() {
        int price = prodotto.getPrice();
        assertEquals(1, price);
    }

    @Test
    void getStock() {
        int stock = prodotto.getStock();
        assertEquals(1, stock);
    }
}