package com.proyecto.congreso.asistencia.service;

import com.proyecto.congreso.asistencia.dto.AssistanceResponse;
import com.proyecto.congreso.asistencia.model.Asistencia;
import com.proyecto.congreso.asistencia.model.Conferencia;
import com.proyecto.congreso.asistencia.repository.AsistenciaRepository;
import com.proyecto.congreso.asistencia.repository.ConferenceRepository;
import com.proyecto.congreso.shared.eventos.AssistanceRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar asistencias a conferencias.
 * Principios aplicados:
 * - Desacoplamiento mediante eventos
 * - Responsabilidad única: solo gestiona asistencias
 * - No conoce la implementación de otros módulos
 *  * PRINCIPIOS APLICADOS:
 *  * - Solo conoce su propio dominio (asistencia + conferencia)
 *  * - NO conoce Pass ni MySQL
 *  * - Publica eventos para comunicarse con otros módulos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssistanceService {

    private final AsistenciaRepository asistenciaRepository;
    private final ConferenceRepository conferenceRepository;
    private final ApplicationEventPublisher eventPublisher;
    /**
     * Marca la asistencia a una conferencia.
     /**
     * ✅ FLUJO CORRECTO:
     * 1. Valida que la conferencia existe (MongoDB)
     * 2. Verifica duplicados
     * 3. Registra asistencia en MongoDB
     * 4. Publica evento para que el módulo 'pases' sume puntos
     * Este método NO suma puntos directamente, solo publica el evento.
     * La suma de puntos la realiza PassPointsEventHandler de forma asíncrona.
     */

    @Transactional
    public AssistanceResponse marcarAsistencia(Long passId, Long conferenciaId) {
        log.info("📋 Marcando asistencia: Pass={}, Conferencia={}", passId, conferenciaId);

        // 1. Validar que la conferencia existe
        Conferencia conferencia = conferenceRepository.findById(conferenciaId)
                .orElseThrow(() -> {
                    log.error("❌ Conferencia no encontrada: {}", conferenciaId);
                    return new IllegalArgumentException("Conferencia no encontrada: " + conferenciaId);
                });

        // 2. Verificar duplicados
        if (asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId)) {
            log.warn("⚠️ Asistencia duplicada: Pass={}, Conferencia={}", passId, conferenciaId);
            throw new IllegalArgumentException(
                    "Ya existe registro de asistencia para este Pass y conferencia"
            );
        }

        // 3. Crear registro de asistencia en MongoDB
        // NOTA: No conocemos participantId aquí, el módulo pases lo resolverá
        Asistencia asistencia = new Asistencia();
        asistencia.setPassId(passId);
        asistencia.setConferenciaId(conferenciaId);
        asistencia.setTituloConferencia(conferencia.getTitulo());
        asistencia.setPuntosOtorgados(conferencia.getPuntos());
        asistencia.setFechaAsistencia(java.time.LocalDateTime.now());
        asistencia.setStatus("PROCESADA");

        asistencia = asistenciaRepository.save(asistencia);
        log.info("✅ Asistencia registrada en MongoDB: ID={}", asistencia.getId());

        // 4. Publicar evento para que el módulo 'pases' sume puntos
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

    /**
     * Obtiene el historial de asistencias de un Pass.
     */
    @Transactional(readOnly = true)
    public List<AssistanceResponse> getAsistenciasByPass(Long passId) {
        log.debug("🔍 Obteniendo asistencias del Pass: {}", passId);
        return asistenciaRepository.findByPassId(passId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de asistentes a una conferencia.
     */
    @Transactional(readOnly = true)
    public List<AssistanceResponse> getAsistenciasByConferencia(Long conferenciaId) {
        log.debug("🔍 Obteniendo asistentes a la conferencia: {}", conferenciaId);
        return asistenciaRepository.findByConferenciaId(conferenciaId).stream()
                .map(AssistanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total de puntos acumulados por asistencias.
     * NOTA: Este es un cálculo informativo basado en MongoDB.
     * El balance real está en Pass (MySQL).
     */
    @Transactional(readOnly=true)
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
    @Transactional(readOnly = true)
    public long countAsistenciasByPass(Long passId) {
        log.debug("🔍 Contando asistencias del Pass: {}", passId);
        return asistenciaRepository.countByPassId(passId);
    }
}
