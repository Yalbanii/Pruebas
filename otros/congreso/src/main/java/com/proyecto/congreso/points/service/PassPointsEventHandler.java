package com.proyecto.congreso.points.service;


import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import com.proyecto.congreso.shared.eventos.CertificateEvent;
import com.proyecto.congreso.shared.eventos.SpecialAccessEvent;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassPointsEventHandler {

    private final PassRepository passRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PassPointsCalculatorFactory passPointsCalculator; // Inyectamos la estrategia

    /**
     * Escucha la asistencia a conferencias, suma puntos y verifica umbrales.
     */
    @EventListener
    @Async
    @Transactional
    public void handleAssistance(AssistancePointsEvent event) {

        Optional<Pass> optionalPass = passRepository.findById(event.getPassId());
        if (optionalPass.isEmpty()) {
            log.warn(" Pass ID {} no encontrado para la acumulación de puntos.", event.getPassId());
            return;
        }

        Pass pass = optionalPass.get();
        Integer pointsToAdd = event.getPointsAfter();

        Integer oldBalance = pass.getPointsBalance();
        Integer newBalance = passPointsCalculator.addPoints(pass, pointsToAdd); // Aqui se usa el calculador

        pass.setPointsBalance(newBalance);

        // CHEQUEO DE CERTIFICADO (25 PUNTOS)
        if (oldBalance < pass.getPointsCertificate() && newBalance >= pass.getPointsCertificate()) {
            // Cambia estado y publica evento.
            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            eventPublisher.publishEvent(new CertificateEvent(pass.getPassId()));
            log.info("✅ Desbloqueaste Logro: Certificado para Pass ID: {}", pass.getPassId());
        }

        // 2. CHEQUEO DE ACCESO ESPECIAL (30 PUNTOS)
        if (oldBalance < pass.getPointsSpecialAccess() && newBalance >= pass.getPointsSpecialAccess()) {
            // Cambiar estado y publicar evento.
            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            eventPublisher.publishEvent(new SpecialAccessEvent(pass.getPassId()));
            log.info("✅ Desbloqueaste Logro: Acceso a Eventos Especiales para Pass ID: {}", pass.getPassId());
        }

        pass.setUpdatedAt(LocalDateTime.now());
        passRepository.save(pass);

        log.info("✅ Puntos agregados a Pass ID {}: +{} (Nuevo Balance={})",
                pass.getPassId(), pointsToAdd, newBalance);
    }
}
