package com.proyecto.congreso.points.exchange.dto;

import com.proyecto.congreso.points.exchange.model.Exchange;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeResponseTest {

    private static final String ID = "EX-001";
    private static final Long PASS_ID = 50L;
    private static final Long PARTICIPANT_ID = 900L;
    private static final String FREEBIE_ID = "F003";
    private static final String ARTICULO = "USB 32GB (Logo)";
    private static final Integer COSTO = 15;
    private static final LocalDateTime FECHA = LocalDateTime.of(2025, 10, 5, 12, 0);

    // -------------------------------------------------------------------------
    // Test: Constructor, Getters y Setters (POJO / Lombok)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // Given
        ExchangeResponse response = new ExchangeResponse(
                ID, PASS_ID, PARTICIPANT_ID, FREEBIE_ID, ARTICULO, COSTO, FECHA
        );

        // Then
        assertNotNull(response, "El objeto no debe ser nulo.");
        assertEquals(ID, response.getId(), "El ID no coincide.");
        assertEquals(PASS_ID, response.getPassId(), "El Pass ID no coincide.");
        assertEquals(ARTICULO, response.getArticulo(), "El artículo no coincide.");
        assertEquals(COSTO, response.getCosto(), "El costo no coincide.");
        assertEquals(FECHA, response.getFechaIntercambio(), "La fecha no coincide.");
    }

    @Test
    void noArgsConstructor_andSetters_shouldWorkCorrectly() {
        // Given
        ExchangeResponse response = new ExchangeResponse();

        // When
        response.setId(ID);
        response.setCosto(COSTO);
        response.setArticulo(ARTICULO);

        // Then
        assertEquals(ID, response.getId());
        assertEquals(COSTO, response.getCosto());
        assertEquals(ARTICULO, response.getArticulo());
        assertNull(response.getPassId(), "Los campos no seteados deben ser nulos.");
    }

    // -------------------------------------------------------------------------
    // Test: Mapeo (fromEntity)
    // -------------------------------------------------------------------------

    @Test
    void fromEntity_shouldMapExchangeEntityCorrectly() {
        // Given
        Exchange mockExchange = mock(Exchange.class);

        // Simular el comportamiento de la entidad (Repo)
        when(mockExchange.getId()).thenReturn(ID);
        when(mockExchange.getPassId()).thenReturn(PASS_ID);
        when(mockExchange.getParticipantId()).thenReturn(PARTICIPANT_ID);
        when(mockExchange.getFreebieId()).thenReturn(FREEBIE_ID);
        when(mockExchange.getArticulo()).thenReturn(ARTICULO);
        when(mockExchange.getPuntosReducidos()).thenReturn(COSTO); // El DTO llama a esto 'costo'
        when(mockExchange.getFechaIntercambio()).thenReturn(FECHA);

        // When
        ExchangeResponse response = ExchangeResponse.fromEntity(mockExchange);

        // Then
        assertNotNull(response);
        assertEquals(ID, response.getId());
        assertEquals(PASS_ID, response.getPassId());
        assertEquals(FREEBIE_ID, response.getFreebieId());
        // Verificación de mapeo de nombre de campo: PuntosReducidos -> Costo
        assertEquals(COSTO, response.getCosto(), "Debe mapear puntosReducidos a costo.");
        assertEquals(FECHA, response.getFechaIntercambio());
    }
}







