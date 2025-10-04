package com.proyecto.congreso.asistencia.controller;

import com.proyecto.congreso.asistencia.model.Asistencia;
import com.proyecto.congreso.asistencia.repository.AsistenciaRepository;
import com.proyecto.congreso.shared.eventos.ConferenceAttendanceTriggerEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/simular")
@RequiredArgsConstructor
@Tag(name = "Simular", description = "Simulacion de Asistencia")
public class AsistenciaSimuladorController {
    private final AsistenciaRepository asistenciaRepository;
    private final ApplicationEventPublisher events;

    // Endpoint para simular la asistencia a una conferencia
    @PostMapping("/asistencia")
    public ResponseEntity<String> simularAsistencia(
            @RequestParam Long passId,
            @RequestParam Long conferenceId) {

        // 1. Crear el registro de asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setPassId(passId);
        asistencia.setConferenciaId(conferenceId);
        asistencia.setFechaAsistencia(LocalDateTime.now());
        asistenciaRepository.save(asistencia);

        // 2. Publicar el evento de disparo (TRIGGER)
        events.publishEvent(new ConferenceAttendanceTriggerEvent(passId, conferenceId));

        return ResponseEntity.ok(
                String.format("Asistencia de Pass %d a Conferencia %d registrada y evento disparado.",
                        passId, conferenceId)
        );
    }
}
