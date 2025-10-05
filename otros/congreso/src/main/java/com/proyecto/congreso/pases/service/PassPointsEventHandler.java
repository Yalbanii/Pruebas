package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.shared.eventos.AssistanceRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Handler que escucha eventos de asistencias registradas y actualiza los puntos del Pass.
 *
 * FLUJO:
 * 1. Módulo 'asistencia' publica AssistanceRegisteredEvent
 * 2. Este handler lo escucha de forma asíncrona
 * 3. Busca el Pass en MySQL
 * 4. Suma los puntos
 * 5. Verifica logros (certificado, acceso especial)
 * 6. Guarda en MySQL
 *
 * PRINCIPIOS:
 * - Procesamiento asíncrono (@Async)
 * - Transaccional para garantizar consistencia
 * - No conoce el módulo 'asistencia'
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PassPointsEventHandler {
    private final PassRepository passRepository;


    @EventListener
    @Async
    @Transactional
    public void handleAssistanceRegistered(AssistanceRegisteredEvent event) {

        log.info("🎧 Evento recibido: AssistanceRegisteredEvent - Pass={}, Puntos={}",
                event.getPassId(), event.getAmountPoints());

        try {
            // 1. Buscar el Pass
            Pass pass = passRepository.findById(event.getPassId())
                    .orElseThrow(() -> {
                        log.error("❌ Pass no encontrado: {}", event.getPassId());
                        return new IllegalArgumentException("Pass not found: " + event.getPassId());
                    });

            // 2. Validar que el Pass esté activo
            if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
                log.warn("⚠️ Pass {} no está activo. Estado: {}. No se suman puntos.",
                        pass.getPassId(), pass.getStatus());
                return;
            }

            // 3. Guardar balance anterior para logs
            Integer balanceAnterior = pass.getPointsBalance();

            // 4. Sumar puntos
            Integer nuevoBalance = balanceAnterior + event.getAmountPoints();
            pass.setPointsBalance(nuevoBalance);
            pass.setPointsAdd(Pass.PointsMovementAdd.ADD);
            pass.setUpdatedAt(LocalDateTime.now());

            // 5. Verificar logros (certificado y acceso especial)
            checkAchievements(pass, balanceAnterior, nuevoBalance);

            // 6. Guardar en MySQL
            passRepository.save(pass);

            log.info("✅ Puntos sumados exitosamente: Pass={}, Puntos={}, Balance: {} → {}",
                    pass.getPassId(), event.getAmountPoints(), balanceAnterior, nuevoBalance);

        } catch (Exception e) {
            log.error("❌ Error procesando AssistanceRegisteredEvent: {}", event, e);
            // En un sistema real, aquí podrías publicar un evento de compensación
            // o marcar la asistencia como "FALLIDA" para reprocesarla con el batch job
        }
    }

    /**
     * Verifica si el Pass alcanzó algún logro al sumar puntos.
     */
    private void checkAchievements(Pass pass, Integer oldBalance, Integer newBalance) {
        // Verificar Certificado (25 puntos)
        if (oldBalance < pass.getPointsCertificate() &&
                newBalance >= pass.getPointsCertificate()) {

            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            log.info("🏆 ¡Certificado alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);
        }

        // Verificar Acceso Especial (30 puntos)
        if (oldBalance < pass.getPointsSpecialAccess() &&
                newBalance >= pass.getPointsSpecialAccess()) {

            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            log.info("🎉 ¡Acceso Especial alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);
        }
    }
}