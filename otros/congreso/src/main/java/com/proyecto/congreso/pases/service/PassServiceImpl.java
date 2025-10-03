package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.shared.eventos.ExchangeFailedEvent;
import com.proyecto.congreso.shared.eventos.ExchangeRequestedEvent;
import com.proyecto.congreso.shared.eventos.FreebieStockReservedEvent;
import com.proyecto.congreso.shared.eventos.PassAdquiredEvent;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PassServiceImpl implements PassService {


    private final PassRepository passRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random();

    @Override
    public Pass createPass(Pass pass) {
        log.debug("Creating Pass for Participant: {}", pass.getParticipantId());

        // Validar que el Participante existe
        Participant participant = participantRepository.findById(pass.getParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + pass.getParticipantId()));

        // Validar que el Participante esté activo
        if (participant.getStatus() != Participant.ParticipantStatus.ACTIVE) {
            throw new IllegalArgumentException("Participant is not active");
        }

        // Establecer estado inicial
        pass.setStatus(Pass.PassStatus.ACTIVE);

        // Balance inicial de puntos (0 por defecto)
        if (pass.getPointsBalance() == null) {
            pass.setPointsBalance(0);
        }

        // Validar balance inicial de puntos no negativo
        if (pass.getPointsBalance().compareTo(0) < 0) {
            throw new IllegalArgumentException("Initial balance of points cannot be negative");
        }

        Pass savedPass = passRepository.save(pass);
        log.info("Pass created successfully: {}", savedPass.getPassId());

        // Publicar evento
        PassAdquiredEvent event = new PassAdquiredEvent(
                savedPass.getPassId(),
                savedPass.getPassType().toString(),
                savedPass.getPointsBalance(),
                participant.getParticipantId(),
                participant.getEmail(),
                LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
        log.debug("PassAdquiredEvent published for pass ID: {}", savedPass.getPassId());

        return savedPass;
    }


    @Override
    @Transactional(readOnly = true)
    public Pass getPassById(Long id) {
        log.debug("Getting Pass by id: {}", id);
        return passRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pass not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pass> getAllPass() {
        log.debug("Getting all Pass");
        return passRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pass> getPassByParticipantId(Long participantId) {
        log.debug("Getting Pass for participant: {}", participantId);
        return passRepository.findByParticipantId(participantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pass> getActivePassByParticipantId(Long participantId) {
        log.debug("Getting active Pass for Participant: {}", participantId);
        return passRepository.findActivePassByParticipantId(participantId);
    }

    @Override
    public Pass updatePass(Long id, Pass pass) {
        log.debug("Updating Pass with id: {}", id);

        Pass existingPass = getPassById(id);

        // Solo permitir actualizar ciertos campos
        existingPass.setPassType(pass.getPassType());

        Pass updatedPass = passRepository.save(existingPass);
        log.info("Pass updated successfully: {}", id);
        return updatedPass;
    }

    @Override
    public void deletePass(Long id) {
        log.debug("Deleting Pass with id: {}", id);

        Pass pass = getPassById(id);

        // Validar que el balance de puntos sea 0
        if (pass.getPointsBalance().compareTo(0) != 0) {
            throw new IllegalArgumentException("Cannot delete Pass with non-zero balance of points");
        }

        // Soft delete: cambiar estado a CLOSED
        pass.setStatus(Pass.PassStatus.CLOSED);
        passRepository.save(pass);

        log.info("Account soft deleted (closed): {}", id);
    }

    @Override
    public Pass activatePass(Long id) {
        log.debug("Activating Pass with id: {}", id);

        Pass pass = getPassById(id);
        pass.setStatus(Pass.PassStatus.ACTIVE);

        Pass activatedPass = passRepository.save(pass);
        log.info("Pass activated: {}", id);
        return activatedPass;
    }

    @Override
    public Pass closePass(Long id) {
        log.debug("Closing Pass with id: {}", id);

        Pass pass = getPassById(id);

        // Validar que el balance sea 0
        if (pass.getPointsBalance().compareTo(0) != 0) {
            throw new IllegalArgumentException("Cannot close Pass with non-zero balance. Current balance of points: " + pass.getPointsBalance());
        }

        pass.setStatus(Pass.PassStatus.CLOSED);

        Pass closedPass = passRepository.save(pass);
        log.info("Pass closed: {}", id);

        return closedPass;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pass> getPassByStatus(Pass.PassStatus status) {
        log.debug("Getting Pass by status: {}", status);
        return passRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pass> getPassByType(Pass.PassType passType) {
        log.debug("Getting Pass by type: {}", passType);
        return passRepository.findByPassType(passType);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPassByParticipantId(Long participantId) {
        return passRepository.countByParticipantId(participantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPassId(Long passId) {
        return passRepository.existsByPassId(passId);
    }

    // Método llamado desde el controlador REST
    @Transactional
    public void startExchange(Long passId, Long freebieId) {
        // Validación básica, ej: Pass existe.
        eventPublisher.publishEvent(new ExchangeRequestedEvent(passId, freebieId));
    }

    // Escucha el evento de éxito
    @EventListener
    @Async
    @Transactional // IMPORTANTE: Transacción local para reducir puntos
    public void handleStockReserved(FreebieStockReservedEvent event) {
        // Buscar la entidad Pass
        passRepository.findById(event.getPassId()).ifPresentOrElse(
                pass -> {
                    Integer costo = event.getCosto();

                    // 2. Lógica de Transacción: Verificar y Descontar Puntos
                    if (pass.getPointsBalance() >= costo) {
                        pass.setPointsBalance(pass.getPointsBalance() - costo);
                        passRepository.save(pass);
                        System.out.println("Puntos descontados para Pass ID: " + pass.getPassId());
                        // Transacción completada
                    } else {
                        // 3. FALLO: Puntos insuficientes. Publicar evento de fallo
                        //    para que el PointsServiceImpl revierta la reserva de stock.
                        eventPublisher.publishEvent(new ExchangeFailedEvent(
                                pass.getPassId(),
                                "Puntos insuficientes para el Freebie ID " + event.getFreebieId()
                        ));
                    }
                },
                // Si el Pass no existe se publica un fallo.
                () -> eventPublisher.publishEvent(new ExchangeFailedEvent(
                        event.getPassId(),
                        "El Pass de usuario no fue encontrado para descontar puntos."
                ))
        );

    }
}
