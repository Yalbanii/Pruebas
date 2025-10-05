package com.proyecto.congreso.points.exchange.controller;

import com.proyecto.congreso.points.exchange.dto.ExchangeRequest;
import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@org.springframework.test.context.ActiveProfiles("test")
public class ExchangeSimuladorControllerTest {

    private static final String BASE_URL = "/api/intercambios";
    private static final Long PASS_ID = 1L;
    private static final String FREEBIE_ID = "F001";

    @Autowired
    private MockMvc mockMvc; // Simula las peticiones HTTP

    @MockitoBean
    private ExchangeService exchangeService; // Simula la capa de servicio

    // Función de utilidad para crear un ExchangeResponse mock con setters
    private ExchangeResponse createMockResponse(Long passId, String freebieId, Integer costo) {
        ExchangeResponse response = new ExchangeResponse(); // Asume constructor vacío
        response.setPassId(passId);
        response.setFreebieId(freebieId);
        response.setCosto(costo);
        return response;
    }

    // Función de utilidad para serializar objetos DTO
    private static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Test: crearExchange (POST /descontar)
    // -------------------------------------------------------------------------

    @Test
    void shouldCrearExchangeAndReturnOk() throws Exception {
        // Given
        ExchangeRequest request = new ExchangeRequest(PASS_ID, FREEBIE_ID);
        ExchangeResponse mockResponse = createMockResponse(PASS_ID, FREEBIE_ID, 50);

        // Simular el servicio
        when(exchangeService.crearExchange(eq(PASS_ID), eq(FREEBIE_ID)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))

                .andExpect(status().isOk()) // Espera HttpStatus 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passId", is(PASS_ID.intValue())))
                .andExpect(jsonPath("$.freebieId", is(FREEBIE_ID)))
                .andExpect(jsonPath("$.costo", is(50)));
    }

    @Test
    void crearExchange_shouldReturnBadRequestIfRequestIsInvalid() throws Exception {
        // Given
        // @Valid debería fallar si falta un campo requerido (ej. null passId)
        ExchangeRequest invalidRequest = new ExchangeRequest(null, FREEBIE_ID);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))

                .andExpect(status().isBadRequest()); // Espera HttpStatus 400
    }

    // -------------------------------------------------------------------------
    // Test: getExchangesByPass (GET /pass/{passId})
    // -------------------------------------------------------------------------

    @Test
    void shouldGetExchangesByPassIdSuccessfully() throws Exception {
        // Given
        List<ExchangeResponse> mockList = Arrays.asList(
                createMockResponse(PASS_ID, "F001", 50),
                createMockResponse(PASS_ID, "F002", 75)
        );
        when(exchangeService.getExchangesByPass(PASS_ID)).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].passId", is(PASS_ID.intValue())))
                .andExpect(jsonPath("$[1].costo", is(75)));
    }

    // -------------------------------------------------------------------------
    // Test: getTotalPuntosReducidos (GET /pass/{passId}/total-puntos)
    // -------------------------------------------------------------------------

    @Test
    void shouldGetTotalPuntosReducidosSuccessfully() throws Exception {
        // Given
        Integer totalPuntos = 125;
        when(exchangeService.getTotalPuntosReducidos(PASS_ID)).thenReturn(totalPuntos);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}/total-puntos", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(totalPuntos))); // Verifica el cuerpo como string simple
    }

    // -------------------------------------------------------------------------
    // Test: countExchangeByPass (GET /pass/{passId}/count)
    // -------------------------------------------------------------------------

    @Test
    void shouldCountExchangesByPassSuccessfully() throws Exception {
        // Given
        long count = 3L;
        when(exchangeService.countExchangesByPass(PASS_ID)).thenReturn(count);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}/count", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));
    }
}