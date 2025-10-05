package com.proyecto.congreso.pases.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.congreso.pases.dto.PassRequest;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.service.CertificateService;
import com.proyecto.congreso.pases.service.PassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class PassControllerTest {

    private String BASE_URL = "/api/pases";
    private Long PASS_ID = 1L;
    private Long PARTICIPANT_ID = 100L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Usamos @MockBean para los servicios de los que depende el controlador
    @MockitoBean
    private PassService passService;

    @MockitoBean
    private CertificateService certificateService; // Mockear el servicio no usado directamente

    private Pass testPass;
    private PassRequest passRequest;

    @BeforeEach
    void setUp() {
        // Configuración de la entidad (Pass) esperada o devuelta
        testPass = new Pass();
        testPass.setPassId(PASS_ID);
        testPass.setPassType(Pass.PassType.ALL_INCLUDED);
        testPass.setPointsBalance(0);
        testPass.setParticipantId(PARTICIPANT_ID);


        // Configuración del cuerpo de la solicitud (PassRequest)
        passRequest = new PassRequest();
        passRequest.setParticipantId(PARTICIPANT_ID);
        passRequest.setPassType(Pass.PassType.ALL_INCLUDED);
        passRequest.setPointsBalance(0);
    }

    // -------------------------------------------------------------------------
    // POST /api/pases/pass
    // -------------------------------------------------------------------------

    @Test
    void shouldCreatePassSuccessfully() throws Exception {
        // Given
        when(passService.createPass(any(Pass.class))).thenReturn(testPass);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passRequest)))
                .andExpect(status().isCreated())
                // CAMBIO CLAVE: Usar "$.passId" en lugar de "$.id"
                .andExpect(jsonPath("$.passId").value(PASS_ID))
                .andExpect(jsonPath("$.passType").value("ALL_INCLUDED"))
                .andExpect(jsonPath("$.pointsBalance").value(0));

        verify(passService).createPass(any(Pass.class));
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/{id}
    // -------------------------------------------------------------------------

    @Test
    void shouldGetPassByIdSuccessfully() throws Exception {
        // Given
        when(passService.getPassById(PASS_ID)).thenReturn(testPass);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", PASS_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passId").value(PASS_ID))
                .andExpect(jsonPath("$.participantId").value(PARTICIPANT_ID));

        verify(passService).getPassById(PASS_ID);
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/pases
    // -------------------------------------------------------------------------

    @Test
    void shouldGetAllPassesSuccessfully() throws Exception {
        // Given
        Pass anotherPass = new Pass();
        anotherPass.setPassId(2L);
        anotherPass.setPassType(Pass.PassType.ALL_INCLUDED);

        List<Pass> passes = Arrays.asList(testPass, anotherPass);

        when(passService.getAllPass()).thenReturn(passes);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/pases")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].passType").value("ALL_INCLUDED"));

        verify(passService).getAllPass();
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/participants/{participantId}
    // -------------------------------------------------------------------------

    @Test
    void shouldGetPassByParticipantIdSuccessfully() throws Exception {
        // Given
        List<Pass> participantPasses = Collections.singletonList(testPass);
        when(passService.getPassByParticipantId(PARTICIPANT_ID)).thenReturn(participantPasses);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/participants/{participantId}", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].participantId").value(PARTICIPANT_ID));

        verify(passService).getPassByParticipantId(PARTICIPANT_ID);
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/participant/{participantId}/active
    // -------------------------------------------------------------------------

    @Test
    void shouldGetActivePassByParticipantIdSuccessfully() throws Exception {
        // Given
        testPass.setStatus(Pass.PassStatus.ACTIVE);
        List<Pass> activePasses = Collections.singletonList(testPass);
        when(passService.getActivePassByParticipantId(PARTICIPANT_ID)).thenReturn(activePasses);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/participant/{participantId}/active", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(passService).getActivePassByParticipantId(PARTICIPANT_ID);
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/status/{status}
    // -------------------------------------------------------------------------

    @Test
    void shouldGetPassByStatusSuccessfully() throws Exception {
        // Given
        Pass.PassStatus status = Pass.PassStatus.ACTIVE;
        List<Pass> activePasses = Collections.singletonList(testPass);
        when(passService.getPassByStatus(status)).thenReturn(activePasses);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(passService).getPassByStatus(status);
    }

    // -------------------------------------------------------------------------
    // PUT /api/pases/{id}
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdatePassSuccessfully() throws Exception {
        // Given
        PassRequest updateRequest = new PassRequest();
        updateRequest.setPassType(Pass.PassType.ALL_INCLUDED);
        updateRequest.setPointsBalance(0);

        // 💡 SOLUCIÓN: Añadir el participantId, ya que es requerido por la validación
        updateRequest.setParticipantId(PARTICIPANT_ID);

        Pass updatedPassEntity = new Pass();
        updatedPassEntity.setPassId(PASS_ID);
        updatedPassEntity.setPassType(Pass.PassType.ALL_INCLUDED);
        updatedPassEntity.setParticipantId(PARTICIPANT_ID);

        when(passService.updatePass(eq(PASS_ID), any(Pass.class)))
                .thenReturn(updatedPassEntity);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", PASS_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passId").value(PASS_ID))
                // NOTA: Asegúrate de que esta aserción sea correcta, si cambiaste a ALL_INCLUDED,
                // el valor esperado DEBE ser "ALL_INCLUDED"
                .andExpect(jsonPath("$.passType").value("ALL_INCLUDED"));

        verify(passService).updatePass(eq(PASS_ID), any(Pass.class));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/pases/{id}
    // -------------------------------------------------------------------------

    @Test
    void shouldDeletePassSuccessfully() throws Exception {
        // Given
        doNothing().when(passService).deletePass(PASS_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", PASS_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Esperamos 204 No Content

        verify(passService).deletePass(PASS_ID);
    }

    // -------------------------------------------------------------------------
    // PATCH /api/pases/{id}/activate
    // -------------------------------------------------------------------------

    @Test
    void shouldActivatePassSuccessfully() throws Exception {
        // Given
        Pass activatedPass = new Pass();
        activatedPass.setPassId(PASS_ID);
        activatedPass.setStatus(Pass.PassStatus.ACTIVE);

        when(passService.activatePass(PASS_ID)).thenReturn(activatedPass);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/{id}/activate", PASS_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(passService).activatePass(PASS_ID);
    }

    // -------------------------------------------------------------------------
    // PATCH /api/pases/{id}/close
    // -------------------------------------------------------------------------

    @Test
    void shouldClosePassSuccessfully() throws Exception {
        // Given
        Pass closedPass = new Pass();
        closedPass.setPassId(PASS_ID);
        closedPass.setStatus(Pass.PassStatus.CLOSED);

        when(passService.closePass(PASS_ID)).thenReturn(closedPass);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/{id}/close", PASS_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));

        verify(passService).closePass(PASS_ID);
    }

    // -------------------------------------------------------------------------
    // GET /api/pases/participant/{participantId}/count
    // -------------------------------------------------------------------------

    @Test
    void shouldCountPassByParticipantIdSuccessfully() throws Exception {
        // Given
        long expectedCount = 5L;
        when(passService.countPassByParticipantId(PARTICIPANT_ID)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/participant/{participantId}/count", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));

        verify(passService).countPassByParticipantId(PARTICIPANT_ID);
    }
}
