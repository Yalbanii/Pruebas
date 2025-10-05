package com.proyecto.congreso.points.assistance;

import com.proyecto.congreso.points.assistance.dto.AssistanceRequest;
import com.proyecto.congreso.points.assistance.dto.AssistanceResponse;
import com.proyecto.congreso.points.assistance.service.AssistanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
public class AsistenciaSimuladorControllerTest {


    private static final String BASE_URL = "/api/asistencias";
    private static final Long PASS_ID = 1L;
    private static final String CONFERENCIA_ID = "C001";
    private static final Long PARTICIPANT_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistanceService assistanceService; // Simula la capa de servicio

    // Función de utilidad para crear un AssistanceResponse mock con setters
    private AssistanceResponse createMockResponse(Long passId, String confId, Integer puntos) {
        AssistanceResponse response = new AssistanceResponse();
        response.setId("AS-MOCK-" + passId + "-" + confId);
        response.setPassId(passId);
        response.setParticipantId(PARTICIPANT_ID);
        response.setConferenciaId(confId);
        response.setPuntosOtorgados(puntos);
        response.setFechaAsistencia(LocalDateTime.now());
        response.setStatus("COMPLETADA");
        return response;
    }

    // Función de utilidad para serializar objetos DTO (requiere jackson-databind)
    private static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // -------------------------------------------------------------------------
    // Test: marcarAsistencia (POST /marcar)
    // -------------------------------------------------------------------------

    @Test
    void shouldMarcarAsistenciaAndReturnCreated() throws Exception {
        // Given
        AssistanceRequest request = new AssistanceRequest(PASS_ID, CONFERENCIA_ID);
        AssistanceResponse mockResponse = createMockResponse(PASS_ID, CONFERENCIA_ID, 15);

        // Simular el servicio
        when(assistanceService.marcarAsistencia(eq(PASS_ID), eq(CONFERENCIA_ID)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/marcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))

                .andExpect(status().isCreated()) // Espera HttpStatus 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.passId", is(PASS_ID.intValue())))
                .andExpect(jsonPath("$.conferenciaId", is(CONFERENCIA_ID)))
                .andExpect(jsonPath("$.puntosOtorgados", is(15)));
    }

    @Test
    void marcarAsistencia_shouldReturnBadRequestIfRequestIsInvalid() throws Exception {
        // Given
        // El DTO @Valid debería fallar si falta un campo requerido (ej. null passId)
        AssistanceRequest invalidRequest = new AssistanceRequest(null, CONFERENCIA_ID);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/marcar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))

                .andExpect(status().isBadRequest()); // Espera HttpStatus 400
    }

    // -------------------------------------------------------------------------
    // Test: getAsistenciasByPass (GET /pass/{passId})
    // -------------------------------------------------------------------------

    @Test
    void shouldGetAsistenciasByPassIdSuccessfully() throws Exception {
        // Given
        List<AssistanceResponse> mockList = Arrays.asList(
                createMockResponse(PASS_ID, "C001", 10),
                createMockResponse(PASS_ID, "C002", 15)
        );
        when(assistanceService.getAsistenciasByPass(PASS_ID)).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].passId", is(PASS_ID.intValue())))
                .andExpect(jsonPath("$[1].puntosOtorgados", is(15)));
    }

    // -------------------------------------------------------------------------
    // Test: getAsistenciasByConferencia (GET /conferencia/{conferenciaId})
    // -------------------------------------------------------------------------

    @Test
    void shouldGetAsistenciasByConferenciaIdSuccessfully() throws Exception {
        // Given
        List<AssistanceResponse> mockList = Arrays.asList(
                createMockResponse(1L, CONFERENCIA_ID, 10),
                createMockResponse(2L, CONFERENCIA_ID, 10)
        );
        when(assistanceService.getAsistenciasByConferencia(CONFERENCIA_ID)).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/conferencia/{conferenciaId}", CONFERENCIA_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].conferenciaId", is(CONFERENCIA_ID)));
    }


    // -------------------------------------------------------------------------
    // Test: getTotalPuntosAcumulados (GET /pass/{passId}/total-puntos)
    // -------------------------------------------------------------------------

    @Test
    void shouldGetTotalPuntosAcumuladosSuccessfully() throws Exception {
        // Given
        Integer totalPuntos = 150;
        when(assistanceService.getTotalPuntosAcumulados(PASS_ID)).thenReturn(totalPuntos);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}/total-puntos", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(totalPuntos)));
    }

    // -------------------------------------------------------------------------
    // Test: countAsistenciasByPass (GET /pass/{passId}/count)
    // -------------------------------------------------------------------------

    @Test
    void shouldCountAsistenciasByPassSuccessfully() throws Exception {
        // Given
        long count = 7L;
        when(assistanceService.countAsistenciasByPass(PASS_ID)).thenReturn(count);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pass/{passId}/count", PASS_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(count)));
    }
}