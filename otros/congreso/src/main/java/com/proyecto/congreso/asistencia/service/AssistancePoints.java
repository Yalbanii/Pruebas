package com.proyecto.congreso.asistencia.service;
import com.proyecto.congreso.asistencia.model.Conferencia;
import com.proyecto.congreso.points.repository.ConferenceRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.shared.eventos.ConferenceAttendanceTriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Slf4j // Para usar 'log.error'
@RequiredArgsConstructor
public class AssistancePoints {
    private final PassRepository passRepository;
    private final ConferenceRepository conferenceRepository; // Para obtener los puntos
    private final ApplicationEventPublisher events;

    @EventListener
    @Async
    @Transactional
    public void handlePointsApplication(AssistancePointsEvent event) {
        Optional<Pass> optionalPass = passRepository.findById(event.getPassId());

        if (optionalPass.isEmpty()) {
            log.error("Fallo de puntos: Pass ID {} no encontrado para sumar puntos.", event.getPassId());
            return;
        }

        Pass pass = optionalPass.get();

        // 1. Sumar Puntos
        Integer puntosActuales = pass.getPointsBalance();
        Integer puntosASumar = event.getPointsAfter(); // El evento debe llevar este dato

        pass.setPointsBalance(puntosActuales + puntosASumar);
        pass.setUpdatedAt(LocalDateTime.now());

        passRepository.save(pass);

        log.info("✅ Puntos sumados a Pass ID {}: {} (Nuevo Balance: {})",
                pass.getPassId(), puntosASumar, pass.getPointsBalance());

        // NOTA: Si necesitas publicar un evento de Auditoría, hazlo aquí.

}
    @EventListener
    @Async
    public void handleAttendanceTrigger(ConferenceAttendanceTriggerEvent event) {

        Optional<Conferencia> optionalConf = conferenceRepository.findById(event.conferenceId());

        if (optionalConf.isEmpty()) {
            log.error("Conferencia ID {} no encontrada. No se pueden otorgar puntos.", event.conferenceId());
            return;
        }

        Conferencia conferencia = optionalConf.get();
        Integer puntosGanados = conferencia.getPuntos(); // Obtener puntos del DTO/Entidad

        // 1. Publicar el evento final con los datos del Pass y los Puntos.
        //    Este evento será escuchado por el módulo 'pases' (para sumar puntos)
        //    y por el módulo de 'log' (para registrar).
        AssistancePointsEvent pointsEvent = new AssistancePointsEvent(
                "ADD", // movementType
                conferencia.getTitulo(), // sourceConferenceId (usando el título como ejemplo)
                null, // targetParticipantId - si lo necesitas, búscalo antes
                event.passId(), // passId
                puntosGanados, // amountPoints
                0, // pointsAfter (0 porque lo calcula el listener de 'pases')
                LocalDateTime.now()
        );

        events.publishEvent(pointsEvent);
        log.info("Evento de Asistencia publicado: Pass ID {} ganó {} puntos.", event.passId(), puntosGanados);
    }

}
