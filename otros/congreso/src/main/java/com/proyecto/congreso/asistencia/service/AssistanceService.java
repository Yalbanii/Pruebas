package com.proyecto.congreso.asistencia.service;

import com.proyecto.congreso.asistencia.dto.AssistanceResponse;
import com.proyecto.congreso.asistencia.model.Asistencia;
import com.proyecto.congreso.asistencia.model.Conferencia;
import com.proyecto.congreso.asistencia.repository.AsistenciaRepository;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.asistencia.repository.ConferenceRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import com.proyecto.congreso.shared.eventos.ConferenceAttendanceTriggerEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar asistencias a conferencias.
 * Principios aplicados:
 * - Desacoplamiento mediante eventos
 * - Responsabilidad única: solo gestiona asistencias
 * - No conoce la implementación de otros módulos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssistanceService {

    private final AsistenciaRepository asistenciaRepository;
    private final PassRepository passRepository;
    private final ConferenceRepository conferenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    /**
     * Marca la asistencia a una conferencia.
     *
     * Flujo:
     * 1. Valida que el Pass existe y está activo
     * 2. Valida que la conferencia existe
     * 3. Verifica que no exista asistencia duplicada
     * 4. Registra la asistencia en MongoDB
     * 5. Publica evento para que el módulo Points sume los puntos
     *
     * Este método NO suma puntos directamente, solo publica el evento.
     * La suma de puntos la realiza PassPointsEventHandler de forma asíncrona.
     */
    @Transactional
    public AssistanceResponse marcarAsistencia(Long passId, Long conferenciaId) {
        log.info("📋 Marcando asistencia: Pass={}, Conferencia={}", passId, conferenciaId);

        // 1. Validar que el Pass existe y está activo
        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> {
                    log.error("❌ Pass no encontrado: {}", passId);
                    return new IllegalArgumentException("Pass no encontrado: " + passId);
                });

        if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
            log.error("❌ El Pass {} no está activo. Estado actual: {}", passId, pass.getStatus());
            throw new IllegalArgumentException("El Pass no está activo");
        }

        // 2. Validar que la conferencia existe
        Conferencia conferencia = conferenceRepository.findById(conferenciaId)
                .orElseThrow(() -> {
                    log.error("❌ Conferencia no encontrada: {}", conferenciaId);
                    return new IllegalArgumentException("Conferencia no encontrada: " + conferenciaId);
                });

        // 3. Verificar duplicados
        if (asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId)) {
            log.warn("⚠️ Ya existe registro de asistencia: Pass={}, Conferencia={}", passId, conferenciaId);
            throw new IllegalArgumentException(
                    "Ya existe registro de asistencia para este Pass y conferencia"
            );
        }

        // 4. Crear registro de asistencia en MongoDB
        Asistencia asistencia = Asistencia.crear(
                passId,
                pass.getParticipantId(),
                conferenciaId,
                conferencia.getTitulo(),
                conferencia.getPuntos()
        );
        asistencia = asistenciaRepository.save(asistencia);
        log.info("✅ Asistencia registrada en MongoDB: ID={}", asistencia.getId());

        // 5. Publicar evento para suma de puntos
        // IMPORTANTE: El módulo 'asistencia' NO suma puntos directamente
        // Solo publica el evento y el módulo 'points' lo escucha y procesa
        AssistancePointsEvent event = AssistancePointsEvent.forAttendance(
                passId,
                pass.getParticipantId(),
                conferenciaId,
                conferencia.getTitulo(),
                conferencia.getPuntos()
        );

        eventPublisher.publishEvent(event);
        log.info("📢 Evento AssistancePointsEvent publicado: Pass={}, Puntos={}",
                passId, conferencia.getPuntos());

        return AssistanceResponse.fromEntity(asistencia);
    }

    /**
     * Obtiene el historial de asistencias de un Pass.
     */
    @Transactional
    public List<AssistanceResponse> getAsistenciasByPass(Long passId) {
        log.debug("🔍 Obteniendo asistencias del Pass: {}", passId);
        return asistenciaRepository.findByPassId(passId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de asistentes a una conferencia.
     */
    @Transactional
    public List<AssistanceResponse> getAsistenciasByConferencia(Long conferenciaId) {
        log.debug("🔍 Obteniendo asistentes a la conferencia: {}", conferenciaId);
        return asistenciaRepository.findByConferenciaId(conferenciaId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total de puntos acumulados por asistencias.
     * NOTA: Este es un cálculo informativo basado en los registros de asistencia.
     * El balance real de puntos está en el Pass (módulo 'pases').
     */
    @Transactional
    public Integer getTotalPuntosAcumulados(Long passId) {
        log.debug("🔍 Calculando puntos acumulados del Pass: {}", passId);
        List<Asistencia> asistencias = asistenciaRepository.findAsistenciasProcedasByPass(passId);
        return asistencias.stream()
                .mapToInt(Asistencia::getPuntosOtorgados)
                .sum();
    }

    /**
     * Cuenta las asistencias de un Pass.
     */
    @Transactional
    public long countAsistenciasByPass(Long passId) {
        log.debug("🔍 Contando asistencias del Pass: {}", passId);
        return asistenciaRepository.countByPassId(passId);
    }

    @EventListener
    @Async
    public void handleAttendanceTrigger(ConferenceAttendanceTriggerEvent event) {
        // 1. Acceso a MONGO DB (a través del ConferenceRepository)
        //    Tu ConferenceRepository debe ser un MongoRepository.
        Optional<Conferencia> optionalConf = conferenceRepository.findById(event.conferenciaId());

        // ... (Manejo de Optional) ...

        Integer puntosGanados = optionalConf.get().getPuntos(); // Dato extraído de Mongo

        // 2. Publica el evento de Puntos. Este evento SÓLO lleva los datos necesarios
        //    para la actualización final: qué actualizar (PassID) y cuánto (Puntos).
        AssistancePointsEvent pointsEvent = new AssistancePointsEvent(
                // ... otros campos ...
                event.passId(),
                puntosGanados // Puntos ganados que provienen de MONGO
                // ... otros campos ...
        );
        eventPublisher.publishEvent(pointsEvent);
    }
}
