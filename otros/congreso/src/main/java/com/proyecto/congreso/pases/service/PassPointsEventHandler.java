package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import com.proyecto.congreso.pases.events.SpecialAccessEvent;
import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.model.SpecialAccess;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.pases.repository.SpecialAccessRepository;
import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import com.proyecto.congreso.pases.events.CertificateEvent;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassPointsEventHandler {

    private final PassRepository passRepository;
    private final FreebieRepository freebieRepository;
    private final ParticipantRepository participantRepository;
    private final CertificateRepository certificateRepository;
    private final SpecialAccessRepository specialAccessRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ========== SUMAR PUNTOS (Asistencias) ==========
    @EventListener
    @Transactional
    public void handleAssistanceRegistered(AssistanceRegisteredEvent event) {
        log.info("Evento recibido: AssistanceRegisteredEvent - Pass={}, Puntos={}",
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
                log.warn("Pass {} no esta activo. Estado: {}. No se suman puntos.",
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
        }
    }


    // ========== DESCONTAR PUNTOS (Intercambios) ==========
    @EventListener
    @Transactional
    public void handleExchangeRegistered(ExchangeRegisteredEvent event) {
        log.info("📥 Evento recibido: ExchangeRegisteredEvent - Pass={}, Puntos={}",
                event.getPassId(), event.getCosto());

        try {
            // 1. Buscar el Pass
            Pass pass = passRepository.findById(event.getPassId())
                    .orElseThrow(() -> {
                        log.error("❌ Pass no encontrado: {}", event.getPassId());
                        return new IllegalArgumentException("Pass not found: " + event.getPassId());
                    });

            // 2. Validar que el Pass esté activo
            if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
                log.warn("⚠️ Pass {} no esta activo. Estado: {}. No se descuentan puntos.",
                        pass.getPassId(), pass.getStatus());

                // Publicar evento de fallo
                eventPublisher.publishEvent(new ExchangeFailedEvent(
                        event.getPassId(),
                        "El Pass no esta activo"
                ));
                return;
            }

            // 3. Validar puntos suficientes
            if (pass.getPointsBalance() < event.getCosto()) {
                log.error("❌ Puntos insuficientes. Balance: {}, Costo: {}",
                        pass.getPointsBalance(), event.getCosto());

                // Publicar evento de fallo para evitar que se reduzca el stock sin intercambios
                eventPublisher.publishEvent(new ExchangeFailedEvent(
                        event.getPassId(),
                        String.format("Puntos insuficientes. Necesitas %d, tienes %d",
                                event.getCosto(), pass.getPointsBalance())
                ));
                return;
            }

            // 4. Descontar puntos
            Integer balanceAnterior = pass.getPointsBalance();
            Integer nuevoBalance = balanceAnterior - event.getCosto();

            pass.setPointsBalance(nuevoBalance);
            pass.setPointsUse(Pass.PointsMovementUse.USE);
            pass.setUpdatedAt(LocalDateTime.now());

            // 5. Guardar en MySQL
            passRepository.save(pass);

            log.info("✅ Puntos descontados exitosamente: Pass={}, Puntos={}, Balance: {} → {}",
                    pass.getPassId(), event.getCosto(), balanceAnterior, nuevoBalance);

        } catch (Exception e) {
            log.error("❌ Error procesando ExchangeRegisteredEvent: {}", event, e);

            // Publicar evento de fallo
            eventPublisher.publishEvent(new ExchangeFailedEvent(
                    event.getPassId(),
                    "Error interno al descontar puntos: " + e.getMessage()
            ));
        }
    }

    // ========== MANEJO DE FALLOS EN INTERCAMBIOS ==========
    @EventListener
    @Transactional
    public void handleExchangeFailed(ExchangeFailedEvent event) {
        log.error("Evento recibido: ExchangeFailedEvent - Pass={}, Razón={}",
                event.getPassId(), event.getReason());

        log.info("Intenta de nuevo");
    }

    // ========== VERIFICACIÓN Y PUBLICACIÓN DE LOGROS ==========
    private void checkAchievements(Pass pass, Integer oldBalance, Integer newBalance) {

        // ========== CERTIFICADO (25 puntos) ==========
        if (oldBalance < pass.getPointsCertificate() &&
                newBalance >= pass.getPointsCertificate() &&
                pass.getCertificateStatus() == Pass.CertificateStatus.NOT_REACHED) {

            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            log.info("🏆 LOGRO DESBLOQUEADO: Certificado alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);

            try {
                CertificateEvent certificateEvent = new CertificateEvent(pass.getPassId());
                eventPublisher.publishEvent(certificateEvent);

                log.info("📢 Evento CertificateEvent publicado: Pass={}, Puntos={}",
                        pass.getPassId(), newBalance);

            } catch (Exception e) {
                log.error("❌ Error al publicar evento de certificado para Pass: {}",
                        pass.getPassId(), e);
            }
        }

        // ========== ACCESO ESPECIAL (30 puntos) ==========
        if (oldBalance < pass.getPointsSpecialAccess() &&
                newBalance >= pass.getPointsSpecialAccess() &&
                pass.getAccessStatus() == Pass.AccessStatus.NOT_REACHED) {

            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            log.info("🎉 LOGRO DESBLOQUEADO: Acceso Especial alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);

            try {
                Participant participant = participantRepository.findById(pass.getParticipantId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Participante no encontrado: " + pass.getParticipantId()));

                SpecialAccessEvent accessEvent = new SpecialAccessEvent(
                        pass.getPassId(),
                        participant.getParticipantId(),
                        participant.getName() + " " + participant.getLastName(),
                        participant.getEmail()
                );
                eventPublisher.publishEvent(accessEvent);

                log.info("📢 Evento SpecialAccessEvent publicado: Pass={}, Puntos={}",
                        pass.getPassId(), newBalance);

            } catch (Exception e) {
                log.error("❌ Error al publicar evento de acceso especial para Pass: {}",
                        pass.getPassId(), e);
            }
        }
    }

    // ========== LISTENERS DE LOGROS ==========
    @EventListener
    @Transactional
    public void handleCertificateAchieved(CertificateEvent event) {
        log.info("Procesando evento de certificado alcanzado: Pass={}",
                event.getPassId());

        try {
            if (certificateRepository.existsByPassId(event.getPassId())) {
                log.warn("⚠️ Ya existe un certificado para el Pass ID: {}. Ignorando evento duplicado.",
                        event.getPassId());
                return;
            }

            // Obtener el Pass para acceder al participantId
            Pass pass = passRepository.findById(event.getPassId())
                    .orElseThrow(() -> {
                        log.error("❌ Pass no encontrado: {}", event.getPassId());
                        return new IllegalArgumentException("Pass no encontrado: " + event.getPassId());
                    });

            // Obtener el Participante para acceder a nombre y email
            Participant participant = participantRepository.findById(pass.getParticipantId())
                    .orElseThrow(() -> {
                        log.error("❌ Participante no encontrado: {}", pass.getParticipantId());
                        return new IllegalArgumentException(
                                "Participante no encontrado: " + pass.getParticipantId());
                    });

            log.info("Datos obtenidos - Participante: {} {}, Email: {}, Puntos: {}",
                    participant.getName(),
                    participant.getLastName(),
                    participant.getEmail(),
                    pass.getPointsBalance());

            // Crear el certificado usando el factory method
            Certificate certificate = Certificate.create(
                    pass.getPassId(),
                    participant.getParticipantId(),
                    participant.getEmail(),
                    participant.getName() + " " + participant.getLastName(),
                    pass.getPointsBalance()
            );

            // Guardar en MySQL
            Certificate savedCertificate = certificateRepository.save(certificate);

            log.info("✅ Certificado creado exitosamente en MySQL: " +
                            "ID={}, Pass={}, Código={}, Participante={}",
                    savedCertificate.getCertificateId(),
                    savedCertificate.getPassId(),
                    savedCertificate.getCertificateCode(),
                    savedCertificate.getParticipantName());

        } catch (Exception e) {
            log.error("❌ Error al crear certificado para Pass ID: {}", event.getPassId(), e);
        }
    }

    @EventListener
    @Transactional
    public void handleSpecialAccessAchieved(SpecialAccessEvent event) {
        log.info("Procesando evento de acceso especial alcanzado: Pass={}",
                event.passId());

        try {
            if (specialAccessRepository.existsByPassId(event.passId())) {
                log.warn("Ya existe un acceso especial para el Pass ID: {}. Ignorando evento duplicado.",
                        event.passId());
                return;
            }

            Pass pass = passRepository.findById(event.passId())
                    .orElseThrow(() -> {
                        log.error("❌ Pass no encontrado: {}", event.passId());
                        return new IllegalArgumentException("Pass no encontrado: " + event.passId());
                    });

            log.info("Datos obtenidos - Participante: {}, Email: {}, Puntos: {}",
                    event.fullName(),
                    event.email(),
                    pass.getPointsBalance());

            // Crear el acceso especial usando el factory method
            SpecialAccess specialAccess = SpecialAccess.create(
                    pass.getPassId(),
                    event.participantId(),
                    event.email(),
                    event.fullName(),
                    pass.getPointsBalance()
            );

            SpecialAccess savedAccess = specialAccessRepository.save(specialAccess);

            log.info("✅ Acceso Especial creado exitosamente en MySQL: " +
                            "ID={}, Pass={}, Código={}, Participante={}",
                    savedAccess.getAccessId(),
                    savedAccess.getPassId(),
                    savedAccess.getAccessCode(),
                    savedAccess.getParticipantName());

        } catch (Exception e) {
            log.error("❌ Error al crear acceso especial para Pass ID: {}", event.passId(), e);
        }
    }
}