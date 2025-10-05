package com.proyecto.congreso.participantes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.congreso.participantes.dto.ParticipantRequest;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@org.springframework.test.context.ActiveProfiles("test")
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParticipantService participantService;

    private Long PARTICIPANT_ID = 1L;

    private Participant testParticipant;
    private ParticipantRequest participantRequest;


    @BeforeEach
    void setUp() {
        testParticipant = new Participant();
        testParticipant.setParticipantId(PARTICIPANT_ID);
        testParticipant.setName("Juan");
        testParticipant.setLastName("Perez");
        testParticipant.setEmail("juan.perez@example.com");
        testParticipant.setStatus(Participant.ParticipantStatus.ACTIVE);

        participantRequest = new ParticipantRequest();
        participantRequest.setName("Juan");
        participantRequest.setLastName("Perez");
        participantRequest.setEmail("juan.perez@example.com");
        participantRequest.setPhone("5551234567");
    }
    @Test
    void shouldCreateParticipantSuccessfully() throws Exception {
        // Given
        when(participantService.createParticipant(any(Participant.class))).thenReturn(testParticipant);

        // When & Then
        mockMvc.perform(post("/api/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantRequest)));
        mockMvc.perform(post("/api/participants/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.participantId").value(PARTICIPANT_ID))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(participantService).createParticipant(any(Participant.class));
    }
    @Test
    void shouldGetParticipantByIdSuccessfully() throws Exception {
        // Given
        when(participantService.getParticipantById(PARTICIPANT_ID)).thenReturn(testParticipant);

        // When & Then
        mockMvc.perform(get("/api/participants/{id}", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantId").value(PARTICIPANT_ID))
                .andExpect(jsonPath("$.name").value("Juan"));

        verify(participantService).getParticipantById(PARTICIPANT_ID);
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldGetAllParticipantsSuccessfully() throws Exception {
        // Given
        Participant anotherParticipant = new Participant();
        anotherParticipant.setParticipantId(2L);
        anotherParticipant.setName("Ana");
        List<Participant> participants = Arrays.asList(testParticipant, anotherParticipant);

        when(participantService.getAllParticipants()).thenReturn(participants);

        // When & Then
        mockMvc.perform(get("/api/participants/participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[1].name").value("Ana"));

        verify(participantService).getAllParticipants();
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldGetParticipantByStatusSuccessfully() throws Exception {
        // Given
        Participant activeParticipant = testParticipant; // Ya está ACTIVE
        List<Participant> activeParticipants = Collections.singletonList(activeParticipant);
        Participant.ParticipantStatus status = Participant.ParticipantStatus.ACTIVE;

        when(participantService.getParticipantByStatus(status)).thenReturn(activeParticipants);

        // When & Then
        mockMvc.perform(get("/api/participants/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(participantService).getParticipantByStatus(status);
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldUpdateParticipantSuccessfully() throws Exception {
        // Given
        Participant updatedParticipant = new Participant();
        updatedParticipant.setParticipantId(PARTICIPANT_ID);
        updatedParticipant.setName("Juan Updated");
        updatedParticipant.setLastName("Perez");
        updatedParticipant.setEmail("juan.perez.updated@example.com");
        updatedParticipant.setStatus(Participant.ParticipantStatus.ACTIVE);

        // El request tiene "Juan" y "juan.perez@example.com", pero el service devuelve "Juan Updated"
        when(participantService.updateParticipant(eq(PARTICIPANT_ID), any(Participant.class)))
                .thenReturn(updatedParticipant);

        // When & Then
        mockMvc.perform(put("/api/participants/{id}", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Updated")) // Verifica el cambio
                .andExpect(jsonPath("$.email").value("juan.perez.updated@example.com"));

        // Verificamos que el service fue llamado con el ID correcto y un objeto Participant
        verify(participantService).updateParticipant(eq(PARTICIPANT_ID), any(Participant.class));
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldDeleteParticipantSuccessfully() throws Exception {
        // Given
        // Configura el mock para que no haga nada (void method)
        doNothing().when(participantService).deleteParticipant(PARTICIPANT_ID);

        // When & Then
        mockMvc.perform(delete("/api/participants/{id}", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Espera el 204 No Content

        verify(participantService).deleteParticipant(PARTICIPANT_ID);
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldActivateParticipantSuccessfully() throws Exception {
        // Given
        Participant activatedParticipant = testParticipant;
        activatedParticipant.setStatus(Participant.ParticipantStatus.ACTIVE); // Ya debería estar ACTIVE, pero lo confirmamos

        when(participantService.activateParticipant(PARTICIPANT_ID)).thenReturn(activatedParticipant);

        // When & Then
        mockMvc.perform(patch("/api/participants/{id}/activate", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(participantService).activateParticipant(PARTICIPANT_ID);
    }

    // ---------------------------------------------------------------------------------------------------

    @Test
    void shouldDeactivateParticipantSuccessfully() throws Exception {
        // Given
        Participant deactivatedParticipant = new Participant();
        deactivatedParticipant.setParticipantId(PARTICIPANT_ID);
        deactivatedParticipant.setName("Juan");
        deactivatedParticipant.setStatus(Participant.ParticipantStatus.INACTIVE); // El estado clave es INACTIVE

        when(participantService.deactivateParticipant(PARTICIPANT_ID)).thenReturn(deactivatedParticipant);

        // When & Then
        mockMvc.perform(patch("/api/participants/{id}/deactivate", PARTICIPANT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(participantService).deactivateParticipant(PARTICIPANT_ID);
    }
}