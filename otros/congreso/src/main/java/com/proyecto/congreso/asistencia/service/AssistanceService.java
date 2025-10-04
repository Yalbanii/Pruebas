package com.proyecto.congreso.asistencia.service;

import com.proyecto.congreso.asistencia.dto.AssistanceResponse;
import com.proyecto.congreso.asistencia.model.Asistencia;
import com.proyecto.congreso.asistencia.model.Conferencia;
import com.proyecto.congreso.asistencia.repository.AsistenciaRepository;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.points.repository.ConferenceRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistanceService {

    private final AsistenciaRepository asistenciaRepository;
    private final PassRepository passRepository;
    private final ConferenceRepository conferenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Marca la asistencia a una conferencia y publica el evento para sumar puntos.
     */
    @Transactional
    public AssistanceResponse marcarAsistencia(Long passId, Long conferenciaId) {
        log.info("Marcando asistencia: Pass={}, Conferencia={}", passId, conferenciaId);

        // 1. Validar que el Pass existe y está activo
        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> new IllegalArgumentException("Pass no encontrado: " + passId));

        if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
            throw new IllegalArgumentException("El Pass no está activo");
        }

        // 2. Validar que la conferencia existe
        Conferencia conferencia = conferenceRepository.findById(conferenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Conferencia no encontrada: " + conferenciaId));

        // 3. Verificar si ya existe la asistencia (evitar duplicados)
        if (asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId)) {
            throw new IllegalArgumentException("Ya existe registro de asistencia para este Pass y conferencia");
        }

        // 4. Crear el registro de asistencia en MongoDB
        Asistencia asistencia = Asistencia.crear(
                passId,
                pass.getParticipantId(),
                conferenciaId,
                conferencia.getTitulo(),
                conferencia.getPuntos()
        );
        asistencia = asistenciaRepository.save(asistencia);
        log.info("Asistencia registrada en MongoDB: {}", asistencia.getId());

        // 5. Publicar evento para que el módulo Points maneje la suma de puntos
        AssistancePointsEvent event = new AssistancePointsEvent(
                "ASSISTANCE",
                String.valueOf(conferenciaId),
                pass.getParticipantId(),
                passId,
                conferencia.getPuntos(),
                pass.getPointsBalance() + conferencia.getPuntos(),
                LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
        log.info("✅ Evento AssistancePointsEvent publicado para Pass={}, Puntos={}",
                passId, conferencia.getPuntos());

        return AssistanceResponse.fromEntity(asistencia);
    }

    /**
     * Obtener historial de asistencias de un Pass.
     */
    @Transactional
    public List<AssistanceResponse> getAsistenciasByPass(Long passId) {
        log.debug("Obteniendo asistencias del Pass: {}", passId);
        return asistenciaRepository.findByPassId(passId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtener lista de asistentes a una conferencia.
     */
    @Transactional
    public List<AssistanceResponse> getAsistenciasByConferencia(Long conferenciaId) {
        log.debug("Obteniendo asistentes a la conferencia: {}", conferenciaId);
        return asistenciaRepository.findByConferenciaId(conferenciaId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calcular el total de puntos acumulados por asistencias.
     */
    @Transactional
    public Integer getTotalPuntosAcumulados(Long passId) {
        log.debug("Calculando puntos acumulados del Pass: {}", passId);
        List<Asistencia> asistencias = asistenciaRepository.findAsistenciasProcedasByPass(passId);
        return asistencias.stream()
                .mapToInt(Asistencia::getPuntosOtorgados)
                .sum();
    }
}
