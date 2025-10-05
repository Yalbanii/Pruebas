package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import com.proyecto.congreso.pases.events.CertificateEvent;
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
    private final ApplicationEventPublisher eventPublisher;

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


    private void checkAchievements(Pass pass, Integer oldBalance, Integer newBalance) {
        // Verificar Certificado (25 puntos)
        if (oldBalance < pass.getPointsCertificate() &&
                newBalance >= pass.getPointsCertificate()) {

            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            log.info("🏆 LOGRO DESBLOQUEADO: Certificado alcanzado! Pass ID: {}, Puntos actuales: {}",
                    pass.getPassId(), newBalance);
            try {
                Pass passRev = passRepository.findById(pass.getParticipantId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Participante no encontrado: " + pass.getParticipantId()));

                //Publicar evento de certificado alcanzado
                CertificateEvent certificateEvent = new CertificateEvent(
                        pass.getPassId()
                );

                eventPublisher.publishEvent(certificateEvent);

                log.info("📢 Evento CertificateAchievedEvent publicado: Pass={}, Participante={}",
                        pass.getPassId(), pass.getParticipantId());

            } catch (Exception e) {
                log.error("❌ Error al publicar evento de certificado para Pass: {}",
                        pass.getPassId(), e);
            }
        }


            if (oldBalance < pass.getPointsSpecialAccess() &&
                    newBalance >= pass.getPointsSpecialAccess()) {

                pass.setAccessStatus(Pass.AccessStatus.REACHED);
                log.info("🎉 LOGRO DESBLOQUEADO: Acceso Especial alcanzado! Pass ID: {}, Puntos actuales: {}",
                        pass.getPassId(), newBalance);
            }
        }


    }
