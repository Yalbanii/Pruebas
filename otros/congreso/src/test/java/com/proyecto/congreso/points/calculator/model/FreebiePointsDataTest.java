package com.proyecto.congreso.points.calculator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FreebiePointsDataTest {
    private static final String FREEBIE_ID = "10";
    private static final String ARTICULO = "Cuaderno de Post-its";
    private static final Integer COSTO = 8;
    private static final Integer STOCK_INICIAL = 100;
    private static final String DESCRIPCION = "Set de notas adhesivas personalizadas";


    // -------------------------------------------------------------------------
    // Test: Constructor con todos los argumentos (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // Given
        FreebiePointsData data = new FreebiePointsData(
                FREEBIE_ID,
                ARTICULO,
                DESCRIPCION,
                STOCK_INICIAL,
                COSTO,
                STOCK_INICIAL // stockActual igual a stockInicial
        );

        // Then
        assertNotNull(data, "El objeto no debe ser nulo.");
        assertEquals(FREEBIE_ID, data.getFreebieId(), "El ID del freebie no coincide.");
        assertEquals(ARTICULO, data.getArticulo(), "El artículo no coincide.");
        assertEquals(COSTO, data.getCosto(), "El costo no coincide.");
        assertEquals(STOCK_INICIAL, data.getStockInicial(), "El stock inicial no coincide.");
        assertEquals(STOCK_INICIAL, data.getStockActual(), "El stock actual no coincide.");
        assertEquals(DESCRIPCION, data.getDescripcion(), "La descripción no coincide.");
    }

    // -------------------------------------------------------------------------
    // Test: Constructor sin argumentos (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        FreebiePointsData data = new FreebiePointsData(); // Usa el constructor sin argumentos
        Integer newCosto = 12;
        Integer newStock = 50;

        // When
        data.setFreebieId(FREEBIE_ID);
        data.setArticulo(ARTICULO);
        data.setCosto(newCosto);
        data.setStockActual(newStock);

        // Then
        assertEquals(FREEBIE_ID, data.getFreebieId(), "Getter/Setter de ID falló.");
        assertEquals(ARTICULO, data.getArticulo(), "Getter/Setter de Artículo falló.");
        assertEquals(newCosto, data.getCosto(), "Getter/Setter de Costo falló.");
        assertEquals(newStock, data.getStockActual(), "Getter/Setter de Stock Actual falló.");
        // Un campo no seteado debe ser nulo.
        assertNull(data.getStockInicial(), "Stock inicial debe ser nulo si no se setea.");
    }
}
