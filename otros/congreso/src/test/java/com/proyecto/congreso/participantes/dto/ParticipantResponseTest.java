package com.proyecto.congreso.participantes.dto;

import com.proyecto.congreso.participantes.model.Participant;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ParticipantResponseTest {
    // --- Datos Simulados (Mock Data) ---
    private final Long ID_SIMULADO = 101L;
    private final String NOMBRE_SIMULADO = "Alicia";
    private final String APELLIDO_SIMULADO = "Gómez";
    private final String EMAIL_SIMULADO = "alicia@ejemplo.com";
    private final String TELEFONO_SIMULADO = "1234567890";
    private final String NACIONALIDAD_SIMULADA = "ES";
    private final Integer EDAD_SIMULADA = 25;
    private final String AREA_SIMULADA = "Ventas";
    // Asumimos que ParticipantStatus.ACTIVE existe.
    private final Participant.ParticipantStatus ESTADO_SIMULADO = Participant.ParticipantStatus.ACTIVE;
    private final LocalDateTime CREADO_EN_SIMULADO = LocalDateTime.now().minusDays(1);
    private final LocalDateTime ACTUALIZADO_EN_SIMULADO = LocalDateTime.now();

    /**
     * Helper para crear y configurar el Mock de la entidad Participant con todos los datos.
     */
    private Participant configurarMockParticipante() {
        // Crea un mock de la entidad Participant
        Participant participante = mock(Participant.class);

        // Define el comportamiento esperado para cada getter de la entidad
        when(participante.getParticipantId()).thenReturn(ID_SIMULADO);
        when(participante.getName()).thenReturn(NOMBRE_SIMULADO);
        when(participante.getLastName()).thenReturn(APELLIDO_SIMULADO);
        when(participante.getEmail()).thenReturn(EMAIL_SIMULADO);
        when(participante.getPhone()).thenReturn(TELEFONO_SIMULADO);
        when(participante.getNacionality()).thenReturn(NACIONALIDAD_SIMULADA);
        when(participante.getAge()).thenReturn(EDAD_SIMULADA);
        when(participante.getArea()).thenReturn(AREA_SIMULADA);
        when(participante.getStatus()).thenReturn(ESTADO_SIMULADO);
        when(participante.getCreatedAt()).thenReturn(CREADO_EN_SIMULADO);
        when(participante.getUpdatedAt()).thenReturn(ACTUALIZADO_EN_SIMULADO);

        return participante;
    }

    // --- Test de Cobertura para el Método fromEntity ---

    @Test
    void fromEntity_DebeMapearTodosLosCamposCorrectamente() {
        // Arrange
        Participant mockParticipante = configurarMockParticipante();

        // Act: Llama al método estático que queremos probar
        ParticipantResponse respuesta = ParticipantResponse.fromEntity(mockParticipante);

        // Assert: Verifica que la respuesta contenga todos los datos simulados

        // 1. Verificación de campos primarios
        assertEquals(ID_SIMULADO, respuesta.getParticipantId(), "El ID debe ser mapeado.");
        assertEquals(NOMBRE_SIMULADO, respuesta.getName(), "El Nombre debe ser mapeado.");
        assertEquals(APELLIDO_SIMULADO, respuesta.getLastName(), "El Apellido debe ser mapeado.");
        assertEquals(EMAIL_SIMULADO, respuesta.getEmail(), "El Email debe ser mapeado.");
        assertEquals(TELEFONO_SIMULADO, respuesta.getPhone(), "El Teléfono debe ser mapeado.");
        assertEquals(NACIONALIDAD_SIMULADA, respuesta.getNacionality(), "La Nacionalidad debe ser mapeada.");
        assertEquals(EDAD_SIMULADA, respuesta.getAge(), "La Edad debe ser mapeada.");
        assertEquals(AREA_SIMULADA, respuesta.getArea(), "El Área debe ser mapeada.");

        // 2. Verificación de la lógica de conversión (Status a String)
        assertEquals(ESTADO_SIMULADO.name(), respuesta.getStatus(), "El Estado debe ser convertido a String.");

        // 3. Verificación de fechas
        assertEquals(CREADO_EN_SIMULADO, respuesta.getCreatedAt(), "La fecha de Creación debe ser mapeada.");
        assertEquals(ACTUALIZADO_EN_SIMULADO, respuesta.getUpdatedAt(), "La fecha de Actualización debe ser mapeada.");

        // 4. Verificación de llamadas (Opcional, pero bueno para confirmar la interacción)
        verify(mockParticipante, times(1)).getStatus();
    }

    // --- Cobertura Adicional para @Data (Constructores y Getters/Setters) ---

    @Test
    void noArgsConstructor_DebeCrearInstanciaConValoresNulos() {
        // Act (Cubre el constructor sin argumentos)
        ParticipantResponse respuesta = new ParticipantResponse();

        // Assert
        assertNotNull(respuesta);
        assertNull(respuesta.getName(), "El Nombre debe ser null por defecto.");
        assertNull(respuesta.getParticipantId(), "El ID debe ser null por defecto.");
    }

    @Test
    void allArgsConstructor_YSettersDebenFuncionar() {
        // Arrange
        String nombrePrueba = "NombrePrueba";
        Long idPrueba = 99L;

        // Act (Cubre el constructor con todos los argumentos)
        ParticipantResponse respuesta = new ParticipantResponse(
                idPrueba,
                nombrePrueba,
                null, null, null, null, null, null,
                "INACTIVO",
                null, null
        );

        // Assert (Cubre los Getters)
        assertEquals(idPrueba, respuesta.getParticipantId());
        assertEquals(nombrePrueba, respuesta.getName());

        // Cubre un Setter (para garantizar la cobertura de los métodos @Data)
        respuesta.setEmail("nuevo@prueba.com");
        assertEquals("nuevo@prueba.com", respuesta.getEmail());
    }
}
