package com.proyecto.congreso.points.calculator.config;

import com.proyecto.congreso.points.calculator.dto.ConferencePointsData;
import com.proyecto.congreso.points.calculator.model.Conferencia;
import com.proyecto.congreso.points.calculator.model.FreebiePointsData;
import com.proyecto.congreso.points.calculator.model.Freebies;
import com.proyecto.congreso.points.calculator.repository.ConferenceRepository;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.calculator.events.ConferenceDataImportedEvent;
import com.proyecto.congreso.points.calculator.events.FreebieDataImportedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointsDataInitializerTest {
    // Simula las dependencias de los repositorios
    @Mock
    private FreebieRepository freebieRepository;
    @Mock
    private ConferenceRepository conferenceRepository;

    // Inyecta los mocks en la instancia real del servicio a probar
    @InjectMocks
    private PointsDataInitializer pointsDataInitializer;

    // --- Datos de Prueba Consistentes ---
    private final String FREEBIE_ID = "10";
    private final String CONFERENCE_ID = "5";

    /**
     * Helper para crear un DTO FreebiePointsData.
     */
    private FreebiePointsData crearDatosFreebie() {
        // Se asume que FreebiePointsData tiene un constructor o setters accesibles
        return new FreebiePointsData(
                FREEBIE_ID, "Taza", "Taza de cerámica", 50, 10, 50
        );
    }

    /**
     * Helper para crear un DTO ConferencePointsData.
     */
    private ConferencePointsData crearDatosConferencia() {
        // Se asume que ConferencePointsData tiene un constructor o setters accesibles
        return new ConferencePointsData(
                CONFERENCE_ID, "Lunes", "2025-10-20", "10:00", "11:00", "60 min", "Sala A",
                "Plenaria", "Introducción a Modulith", "Dr. Pérez", 15
        );
    }

    // =========================================================================
    // 1. Cobertura del Método handleFreebieDataImport
    // =========================================================================

    @Test
    void handleFreebieDataImport_DebeGuardar_SiElFreebieNoExiste() {
        // Arrange
        FreebiePointsData datosFreebie = crearDatosFreebie();
        FreebieDataImportedEvent evento = new FreebieDataImportedEvent(datosFreebie);

        // Mocks: findById devuelve vacío (no existe)
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.empty());
        // Mock: save debe devolver cualquier Freebies al guardar
        when(freebieRepository.save(any(Freebies.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        pointsDataInitializer.handleFreebieDataImport(evento);

        // Assert
        // Verifica que se intentó guardar la entidad (cubre el camino de conversión y save)
        verify(freebieRepository, times(1)).save(any(Freebies.class));
        // Verifica que se buscó
        verify(freebieRepository, times(1)).findById(FREEBIE_ID);
    }

    @Test
    void handleFreebieDataImport_DebeSaltarYNoGuardar_SiElFreebieYaExiste() {
        // Arrange
        FreebiePointsData datosFreebie = crearDatosFreebie();
        FreebieDataImportedEvent evento = new FreebieDataImportedEvent(datosFreebie);
        Freebies freebieExistente = new Freebies();

        // Mocks: findById devuelve la entidad existente (ya existe)
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(freebieExistente));

        // Act
        pointsDataInitializer.handleFreebieDataImport(evento);

        // Assert
        // Verifica que se buscó
        verify(freebieRepository, times(1)).findById(FREEBIE_ID);
        // Verifica que NUNCA se llamó a save (cubre el camino de 'if (existing.isPresent()) return;')
        verify(freebieRepository, never()).save(any(Freebies.class));
    }

    // =========================================================================
    // 2. Cobertura del Método handleConferenceDataImport
    // =========================================================================

    @Test
    void handleConferenceDataImport_DebeGuardar_SiLaConferenciaNoExiste() {
        // Arrange
        ConferencePointsData datosConferencia = crearDatosConferencia();
        ConferenceDataImportedEvent evento = new ConferenceDataImportedEvent(datosConferencia);

        // Mocks: findById devuelve vacío (no existe)
        when(conferenceRepository.findById(CONFERENCE_ID)).thenReturn(Optional.empty());
        // Mock: save debe devolver cualquier Conferencia al guardar
        when(conferenceRepository.save(any(Conferencia.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        pointsDataInitializer.handleConferenceDataImport(evento);

        // Assert
        // Verifica que se intentó guardar la entidad (cubre el camino de conversión y save)
        verify(conferenceRepository, times(1)).save(any(Conferencia.class));
        // Verifica que se buscó
        verify(conferenceRepository, times(1)).findById(CONFERENCE_ID);
    }

    @Test
    void handleConferenceDataImport_DebeSaltarYNoGuardar_SiLaConferenciaYaExiste() {
        // Arrange
        ConferencePointsData datosConferencia = crearDatosConferencia();
        ConferenceDataImportedEvent evento = new ConferenceDataImportedEvent(datosConferencia);
        Conferencia conferenciaExistente = new Conferencia();

        // Mocks: findById devuelve la entidad existente (ya existe)
        when(conferenceRepository.findById(CONFERENCE_ID)).thenReturn(Optional.of(conferenciaExistente));

        // Act
        pointsDataInitializer.handleConferenceDataImport(evento);

        // Assert
        // Verifica que se buscó
        verify(conferenceRepository, times(1)).findById(CONFERENCE_ID);
        // Verifica que NUNCA se llamó a save (cubre el camino de 'if (existing.isPresent()) return;')
        verify(conferenceRepository, never()).save(any(Conferencia.class));
    }
}

