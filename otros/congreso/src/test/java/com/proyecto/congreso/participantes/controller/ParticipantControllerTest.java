package com.proyecto.congreso.participantes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.congreso.participantes.dto.ParticipantRequest;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.service.ParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// Anotación para probar solo el controlador, sin cargar toda la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Simula el bean del servicio
    @MockitoBean
    private ParticipantService participantService;

    // --- Métodos de Ayuda para Mocks ---

    private Participant createMockParticipant(Long id, Participant.ParticipantStatus status) {
        // Simulación de la Entidad Participant
        Participant participant = new Participant();
        participant.setParticipantId(id);
        participant.setName("Juan");
        participant.setLastName("Perez");
        participant.setEmail("juan@test.com");
        participant.setStatus(status);
        // La clase ParticipantStatus debe ser accesible y funcional
        return participant;
    }

    private ParticipantRequest createMockRequest() {
        // Simulación del DTO de Request
        ParticipantRequest request = new ParticipantRequest();
        request.setName("Juan");
        request.setLastName("Perez");
        request.setEmail("juan@test.com");
        request.setPhone("1234567890"); // Usar 10 dígitos
        request.setNacionality("MX");
        request.setAge(30);
        request.setArea("IT");
        // Si participantId es requerido para POST/PUT, incluirlo aquí, si no, omitir o dejar en null
        // request.setParticipantId(null);
        return request;
    }

    @Test
    void createParticipant_ShouldReturnCreated() throws Exception {
        // Arrange
        ParticipantRequest request = createMockRequest();
        Participant savedParticipant = createMockParticipant(1L, Participant.ParticipantStatus.ACTIVE);

        // Mock: Cuando el servicio es llamado para crear, devuelve el participante guardado
        when(participantService.createParticipant(any(Participant.class))).thenReturn(savedParticipant);

        // Act & Assert
        mockMvc.perform(post("/api/participants/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Espera HTTP 201
                .andExpect(jsonPath("$.name").value("Juan"));

        // Verify: Asegura que el método del servicio fue llamado exactamente una vez.
        verify(participantService, times(1)).createParticipant(any(Participant.class));
    }

    @Test
    void getParticipantById_ShouldReturnParticipant() throws Exception {
        // Arrange
        Long participantId = 2L;
        Participant mockParticipant = createMockParticipant(participantId, Participant.ParticipantStatus.ACTIVE);

        // Mock: Cuando se llama a getParticipantById, devuelve el participante simulado
        when(participantService.getParticipantById(participantId)).thenReturn(mockParticipant);

        // Act & Assert
        mockMvc.perform(get("/api/participants/{id}", participantId))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.email").value("juan@test.com"));

        verify(participantService, times(1)).getParticipantById(participantId);
    }

    @Test
    void getAllParticipants_ShouldReturnList() throws Exception {
        // Arrange
        List<Participant> mockList = List.of(
                createMockParticipant(3L, Participant.ParticipantStatus.ACTIVE),
                createMockParticipant(4L, Participant.ParticipantStatus.INACTIVE)
        );

        // Mock: Devuelve una lista simulada
        when(participantService.getAllParticipants()).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/participants/participants"))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(participantService, times(1)).getAllParticipants();
    }

    @Test
    void getParticipantByStatus_ShouldReturnActiveList() throws Exception {
        // Arrange
        Participant.ParticipantStatus status = Participant.ParticipantStatus.ACTIVE;
        List<Participant> mockList = Collections.singletonList(
                createMockParticipant(5L, status)
        );

        // Mock: Devuelve una lista filtrada por estado
        when(participantService.getParticipantByStatus(status)).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/participants/status/{status}", status))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(participantService, times(1)).getParticipantByStatus(status);
    }

    @Test
    void updateParticipant_ShouldReturnUpdated() throws Exception {
        // Arrange
        Long participantId = 6L;
        ParticipantRequest request = createMockRequest();
        Participant updatedParticipant = createMockParticipant(participantId, Participant.ParticipantStatus.ACTIVE);
        updatedParticipant.setName("Andres"); // Simula el cambio

        // Mock: Cuando se llama a updateParticipant con cualquier Participant, devuelve el objeto actualizado
        when(participantService.updateParticipant(eq(participantId), any(Participant.class))).thenReturn(updatedParticipant);

        // Act & Assert
        mockMvc.perform(put("/api/participants/{id}", participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.name").value("Andres")); // Verifica el cambio simulado

        verify(participantService, times(1)).updateParticipant(eq(participantId), any(Participant.class));
    }

    @Test
    void deleteParticipant_ShouldReturnNoContent() throws Exception {
        Long participantId = 7L;

        // Mock: La llamada al servicio no hace nada (void method)
        doNothing().when(participantService).deleteParticipant(participantId);

        // Act & Assert
        mockMvc.perform(delete("/api/participants/{id}", participantId))
                .andExpect(status().isNoContent()); // Espera HTTP 204

        // Verify: Asegura que el soft delete fue invocado.
        verify(participantService, times(1)).deleteParticipant(participantId);
    }

    @Test
    void activateParticipant_ShouldReturnOk() throws Exception {
        // Arrange
        Long participantId = 8L;
        Participant activatedParticipant = createMockParticipant(participantId, Participant.ParticipantStatus.ACTIVE);

        // Mock: Devuelve el participante con el estado ACTIVADO
        when(participantService.activateParticipant(participantId)).thenReturn(activatedParticipant);

        // Act & Assert
        mockMvc.perform(patch("/api/participants/{id}/activate", participantId))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(participantService, times(1)).activateParticipant(participantId);
    }

    @Test
    void deactivateParticipant_ShouldReturnOk() throws Exception {
        // Arrange
        Long participantId = 9L;
        Participant deactivatedParticipant = createMockParticipant(participantId, Participant.ParticipantStatus.INACTIVE);

        // Mock: Devuelve el participante con el estado DESACTIVADO
        when(participantService.deactivateParticipant(participantId)).thenReturn(deactivatedParticipant);

        // Act & Assert
        mockMvc.perform(patch("/api/participants/{id}/deactivate", participantId))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(participantService, times(1)).deactivateParticipant(participantId);
    }
}
