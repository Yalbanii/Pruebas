package com.proyecto.congreso.asistencia.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import com.proyecto.congreso.shared.eventos.CertificateEvent;
import com.proyecto.congreso.shared.eventos.SpecialAccessEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Handler que escucha eventos de asistencia y suma puntos al Pass.
 *
 * Ubicación correcta: módulo 'points' (NO en 'asistencia')
 * Responsabilidad: Procesar la suma de puntos y verificar logros
 *
 * Principios aplicados:
 * - Event-Driven Architecture
 * - Procesamiento asíncrono
 * - Desacoplamiento total entre módulos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssistancePointsHandler {
    private final PassRepository passRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Escucha el evento de asistencia y suma los puntos correspondientes.
     *
     * Flujo:
     * 1. Busca el Pass
     * 2. Suma los puntos
     * 3. Verifica umbrales de logros (Certificado 25pts, Acceso Especial 30pts)
     * 4. Publica eventos de logros si corresponde
     * 5. Guarda el Pass actualizado
     *
     * Este método se ejecuta de forma ASÍNCRONA.
     */
    @EventListener
    @Async
    @Transactional
    public void handleAssistancePoints(AssistancePointsEvent event) {
        log.info("🎯 Procesando evento de asistencia: Pass={}, Puntos={}",
                event.getPassId(), event.getAmountPoints());

        // 1. Buscar el Pass
        Optional<Pass> optionalPass = passRepository.findById(event.getPassId());
        if (optionalPass.isEmpty()) {
            log.error("❌ Pass ID {} no encontrado para sumar puntos", event.getPassId());
            return;
        }

        Pass pass = optionalPass.get();
        Integer puntosASumar = event.getAmountPoints();
        Integer balanceAnterior = pass.getPointsBalance();
        Integer nuevoBalance = balanceAnterior + puntosASumar;

        // 2. Actualizar balance de puntos
        pass.setPointsBalance(nuevoBalance);
        pass.setUpdatedAt(LocalDateTime.now());

        // 3. Verificar logros ANTES de guardar
        checkAndPublishAchievements(pass, balanceAnterior, nuevoBalance);

        // 4. Guardar cambios
        passRepository.save(pass);

        log.info("✅ Puntos sumados exitosamente: Pass={}, Anterior={}, Sumados={}, Nuevo={}",
                pass.getPassId(), balanceAnterior, puntosASumar, nuevoBalance);
    }

    /**
     * Verifica si se alcanzaron los umbrales de logros y publica eventos.
     */
    private void checkAndPublishAchievements(Pass pass, Integer oldBalance, Integer newBalance) {

        // LOGRO: Certificado (25 puntos)
        if (oldBalance < pass.getPointsCertificate() && newBalance >= pass.getPointsCertificate()) {
            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            eventPublisher.publishEvent(new CertificateEvent(pass.getPassId()));
            log.info("🏆 LOGRO DESBLOQUEADO: Certificado para Pass ID {}", pass.getPassId());
        }

        // LOGRO: Acceso Especial (30 puntos)
        if (oldBalance < pass.getPointsSpecialAccess() && newBalance >= pass.getPointsSpecialAccess()) {
            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            eventPublisher.publishEvent(new SpecialAccessEvent(pass.getPassId()));
            log.info("🏆 LOGRO DESBLOQUEADO: Acceso Especial para Pass ID {}", pass.getPassId());
        }
    }

}
