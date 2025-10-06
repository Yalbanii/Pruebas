package com.proyecto.congreso.points.assistance.service;

import com.proyecto.congreso.points.assistance.dto.AssistanceResponse;
import com.proyecto.congreso.points.assistance.model.Asistencia;
import com.proyecto.congreso.points.calculator.model.Conferencia;
import com.proyecto.congreso.points.assistance.repository.AsistenciaRepository;
import com.proyecto.congreso.points.calculator.repository.ConferenceRepository;
import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistanceService {

    private final AsistenciaRepository asistenciaRepository;
    private final ConferenceRepository conferenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AssistanceResponse marcarAsistencia(Long passId, String conferenciaId) {
        log.info("📋 Marcando asistencia: Pass={}, Conferencia={}", passId, conferenciaId);

        // 1. Validar que la conferencia existe
        Conferencia conferencia = conferenceRepository.findById(conferenciaId)
                .orElseThrow(() -> {
                    log.error("❌ Conferencia no encontrada: {}", conferenciaId);
                    return new IllegalArgumentException("Conferencia no encontrada: " + conferenciaId);
                });

        // 3. Verificar duplicados
        if (asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId)) {
            log.warn("⚠️ Asistencia duplicada: Pass={}, Conferencia={}", passId, conferenciaId);
            throw new IllegalArgumentException(
                    "Ya existe registro de asistencia para este Pass y conferencia"
            );
        }

        // 4. Crear registro de asistencia en MongoDB
        Asistencia asistencia = new Asistencia();
        asistencia.setPassId(passId);
        asistencia.setConferenciaId(conferenciaId);
        asistencia.setTituloConferencia(conferencia.getTitulo());
        asistencia.setPuntosOtorgados(conferencia.getPuntos());
        asistencia.setFechaAsistencia(java.time.LocalDateTime.now());
        asistencia.setStatus("PROCESADA");

        asistencia = asistenciaRepository.save(asistencia);
        log.info("✅ Asistencia registrada en MongoDB: ID={}, Puntos total={}",
                asistencia.getId(), getTotalPuntosAcumulados(passId));

        // 5. Publicar evento para que el m0dulo Pases sume puntos
        AssistanceRegisteredEvent event = new AssistanceRegisteredEvent(
                passId,
                conferenciaId,
                conferencia.getTitulo(),
                conferencia.getPuntos()
        );

        eventPublisher.publishEvent(event);
        log.info("📢 Evento AssistanceRegisteredEvent publicado: Pass={}, Puntos={}",
                passId, conferencia.getPuntos());

        return AssistanceResponse.fromEntity(asistencia);
    }

     // Historial de asistencias de un Pass
    @Transactional(readOnly = true)
    public List<AssistanceResponse> getAsistenciasByPass(Long passId) {
        log.debug("🔍 Obteniendo asistencias del Pass: {}", passId);
        return asistenciaRepository.findByPassId(passId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

     // Obtiene la lista de asistentes a una conferencia
    @Transactional(readOnly = true)
    public List<AssistanceResponse> getAsistenciasByConferencia(String conferenciaId) {
        log.debug("🔍 Obteniendo asistentes a la conferencia: {}", conferenciaId);
        return asistenciaRepository.findByConferenciaId(conferenciaId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Calcula el total de puntos acumulados por asistencias
    @Transactional(readOnly=true)
    public Integer getTotalPuntosAcumulados(Long passId) {
        log.debug("🔍 Calculando puntos acumulados del Pass: {}", passId);
        List<Asistencia> asistencias = asistenciaRepository.findAsistenciasProcedasByPass(passId);
        return asistencias.stream()
                .mapToInt(Asistencia::getPuntosOtorgados)
                .sum();
    }

    // Cuenta las asistencias de un Pass
    @Transactional(readOnly = true)
    public long countAsistenciasByPass(Long passId) {
        log.debug("🔍 Contando asistencias del Pass: {}", passId);
        return asistenciaRepository.countByPassId(passId);
    }

    private static class PassResponse {
        private Long passId;
        private Long participantId;

        public Long getPassId() { return passId; }
        public void setPassId(Long passId) { this.passId = passId; }

        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
    }
}