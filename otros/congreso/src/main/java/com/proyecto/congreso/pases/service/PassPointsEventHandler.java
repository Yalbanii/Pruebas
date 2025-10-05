package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import com.proyecto.congreso.pases.events.CertificateEvent;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import com.proyecto.congreso.points.events.FreebieStockReservedEvent;
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
    private final ApplicationEventPublisher eventPublisher;
    private final CertificateRepository certificateRepository;


    //----------- ADD POINTS -------------
    @EventListener
    @Transactional
    public void handleAssistanceRegistered(AssistanceRegisteredEvent event) {

        log.info(" Evento recibido: AssistanceRegisteredEvent - Pass={}, Puntos={}",
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
                log.warn("Ese Pase {} no está activo. Estado: {}. No se suman puntos.",
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

            log.info("✅ Puntos sumados exitosamente: Pass={}, Puntos={}, Balance of Points: {} → {}",
                    pass.getPassId(), event.getAmountPoints(), balanceAnterior, nuevoBalance);

        } catch (Exception e) {
            log.error("❌ Error procesando AssistanceRegisteredEvent: {}", event, e);
        }
    }

    //----------- USE POINTS -------------
    @EventListener
    @Transactional
    public void handleStockReserved(FreebieStockReservedEvent event) {
        // Buscar la entidad Pass
        passRepository.findById(event.getPassId()).ifPresentOrElse(
                pass -> {
                    Integer costo = event.getCosto();

                    // 2. Lógica de Movimiento: Verificar y Descontar Puntos
                    if (pass.getPointsBalance() >= costo) {
                        pass.setPointsBalance(pass.getPointsBalance() - costo);
                        passRepository.save(pass);
                        System.out.println("Puntos descontados para Pass ID: " + pass.getPassId());

                    } else {
                        // 3. FALLO: Puntos insuficientes. Publicar evento de fallo para que el PointsServiceImpl revierta la reserva de stock.
                        eventPublisher.publishEvent(new ExchangeFailedEvent(
                                pass.getPassId(),
                                "Puntos insuficientes para obtener el Freebie ID " + event.getFreebieId()
                        ));
                    }
                },
                () -> eventPublisher.publishEvent(new ExchangeFailedEvent(
                        event.getPassId(),
                        "El Pass de usuario no fue encontrado para descontar puntos."
                ))
        );

    }


    //----------- LOGROS DESBLOQUEADOS -------------
    //PUBLICA EVENTOS
    private void checkAchievements(Pass pass, Integer oldBalance, Integer newBalance) {

        // Verificar Certificado (25 puntos)
        if (oldBalance < pass.getPointsCertificate() &&
                newBalance >= pass.getPointsCertificate() &&
                pass.getCertificateStatus() == Pass.CertificateStatus.NOT_REACHED) {

            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            log.info("🏆 LOGRO DESBLOQUEADO: Certificado alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);

            try {
                // Publicar evento de certificado alcanzado (solo con passId)
                // El CertificateEventHandler obtendrá el resto de la información
                CertificateEvent certificateEvent = new CertificateEvent(pass.getPassId());

                eventPublisher.publishEvent(certificateEvent);

                log.info("📢 Evento CertificateEvent publicado: Pass={}, Puntos={}",
                        pass.getPassId(), newBalance);

            } catch (Exception e) {
                log.error("❌ Error al publicar evento de certificado para Pass: {}",
                        pass.getPassId(), e);
            }
        }

        // Verificar Acceso Especial (30 puntos)
        if (oldBalance < pass.getPointsSpecialAccess() &&
                newBalance >= pass.getPointsSpecialAccess() &&
                pass.getAccessStatus() == Pass.AccessStatus.NOT_REACHED) {

            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            log.info("🎉 LOGRO DESBLOQUEADO: Acceso Especial alcanzado! Pass ID: {}, Puntos: {}",
                    pass.getPassId(), newBalance);

            // TODO: Publicar SpecialAccessEvent si es necesario
        }
    }

    /**
     * Escucha el evento CertificateEvent y crea el registro en MySQL.
     * <p>
     * FLUJO:
     * 1. Obtiene el Pass para acceder al participantId
     * 2. Obtiene el Participant para acceder a sus datos (nombre, email)
     * 3. Verifica que no exista ya un certificado (evitar duplicados)
     * 4. Crea el registro usando el factory method de Certificate
     * 5. Persiste en MySQL
     */
    @EventListener
    @Transactional
    public void handleCertificateAchieved(CertificateEvent event) {
        log.info("🎓 Procesando evento de certificado alcanzado: Pass={}",
                event.getPassId());

        try {
            // 1. Verificar que no exista duplicado
            if (certificateRepository.existsByPassId(event.getPassId())) {
                log.warn("⚠️ Ya existe un certificado para el Pass ID: {}. Ignorando evento duplicado.",
                        event.getPassId());
                return;
            }

            // 2. Obtener el Pass para acceder al participantId
            Pass pass = passRepository.findById(event.getPassId())
                    .orElseThrow(() -> {
                        log.error("❌ Pass no encontrado: {}", event.getPassId());
                        return new IllegalArgumentException("Pass no encontrado: " + event.getPassId());
                    });

            // 3. Obtener el Participant para acceder a nombre y email
            Participant participant = participantRepository.findById(pass.getParticipantId())
                    .orElseThrow(() -> {
                        log.error("❌ Participante no encontrado: {}", pass.getParticipantId());
                        return new IllegalArgumentException(
                                "Participante no encontrado: " + pass.getParticipantId());
                    });

            log.info("📋 Datos obtenidos - Participante: {} {}, Email: {}, Puntos: {}",
                    participant.getName(),
                    participant.getLastName(),
                    participant.getEmail(),
                    pass.getPointsBalance());

            // 4. Crear el certificado usando el factory method
            Certificate certificate = Certificate.create(
                    pass.getPassId(),
                    participant.getParticipantId(),
                    participant.getEmail(),
                    participant.getName() + " " + participant.getLastName(),
                    pass.getPointsBalance()
            );

            // 5. Persistir en MySQL
            Certificate savedCertificate = certificateRepository.save(certificate);

            log.info("✅ Certificado creado exitosamente en MySQL: " +
                            "ID={}, Pass={}, Código={}, Participante={}",
                    savedCertificate.getCertificateId(),
                    savedCertificate.getPassId(),
                    savedCertificate.getCertificateCode(),
                    savedCertificate.getParticipantName());

        } catch (Exception e) {
            log.error("❌ Error al crear certificado para Pass ID: {}", event.getPassId(), e);
            // No relanzamos la excepción para no afectar el flujo principal de puntos
            // El certificado se puede generar manualmente después si es necesario
        }
    }
}


