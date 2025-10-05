package com.proyecto.congreso.points.calculator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FreebiesTest {
    private static final String ID = "F001";
    private static final String ARTICULO = "Libreta Reciclada";
    private static final Integer COSTO = 10;
    private static final Integer STOCK_INICIAL = 350;

    // -------------------------------------------------------------------------
    // Test: Constructor con todos los argumentos (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateEntityWithAllFieldsSet() {
        // Given
        Freebies freebie = new Freebies(
                ID,
                ARTICULO,
                "Libreta de tapa dura con logo",
                STOCK_INICIAL,
                COSTO,
                STOCK_INICIAL - 50 // Stock actual reducido
        );

        // Then
        assertNotNull(freebie, "La entidad no debe ser nula.");
        assertEquals(ID, freebie.getFreebieId(), "El ID no coincide.");
        assertEquals(ARTICULO, freebie.getArticulo(), "El artículo no coincide.");
        assertEquals(COSTO, freebie.getCosto(), "El costo no coincide.");
        assertEquals(STOCK_INICIAL, freebie.getStockInicial(), "El stock inicial no coincide.");
        assertEquals(STOCK_INICIAL - 50, freebie.getStockActual(), "El stock actual no coincide.");
    }

    // -------------------------------------------------------------------------
    // Test: Constructor sin argumentos (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        Freebies freebie = new Freebies(); // Usa el constructor sin argumentos
        Integer nuevoCosto = 12;
        Integer nuevoStock = 15;

        // When
        freebie.setFreebieId(ID);
        freebie.setArticulo(ARTICULO);
        freebie.setCosto(nuevoCosto);
        freebie.setStockActual(nuevoStock);

        // Then
        assertEquals(ID, freebie.getFreebieId(), "Getter/Setter de ID falló.");
        assertEquals(ARTICULO, freebie.getArticulo(), "Getter/Setter de Artículo falló.");
        assertEquals(nuevoCosto, freebie.getCosto(), "Getter/Setter de Costo falló.");
        assertEquals(nuevoStock, freebie.getStockActual(), "Getter/Setter de Stock Actual falló.");
        assertNull(freebie.getStockInicial(), "Stock inicial debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test: Mapeo de valores nulos
    // -------------------------------------------------------------------------

    @Test
    void shouldHandleNullValuesGracefully() {
        // Given
        Freebies freebie = new Freebies();

        // When
        freebie.setFreebieId(ID);
        freebie.setArticulo(null);
        freebie.setCosto(null);

        // Then
        assertEquals(ID, freebie.getFreebieId(), "El ID debe persistir.");
        assertNull(freebie.getArticulo(), "El artículo debe ser nulo.");
        assertNull(freebie.getCosto(), "El costo debe ser nulo.");
    }
}

