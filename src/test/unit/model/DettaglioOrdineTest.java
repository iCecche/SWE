package test.unit.model;

import main.model.DettaglioOrdine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DettaglioOrdineTest {

    private DettaglioOrdine dettaglioOrdine;

    @BeforeEach
    void setUp() {
        dettaglioOrdine = new DettaglioOrdine(1, 1);
    }

    @Test
    void getQuantity() {
        int quantity = dettaglioOrdine.getQuantity();
        assertEquals(1, quantity);
    }

    @Test
    void getProduct_id() {
        int productId = dettaglioOrdine.getProduct_id();
        assertEquals(1, productId);
    }

    @Test
    void setProduct_id() {
        dettaglioOrdine.setProduct_id(5);
        int productId = dettaglioOrdine.getProduct_id();
        assertEquals(5, productId);
    }

    @Test
    void setQuantity() {
        dettaglioOrdine.setQuantity(10);
        int quantity = dettaglioOrdine.getQuantity();
        assertEquals(10, quantity);
    }
}