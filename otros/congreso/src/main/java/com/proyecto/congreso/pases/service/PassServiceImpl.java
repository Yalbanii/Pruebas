package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.points.exchange.events.ExchangeRequestedEvent;
import com.proyecto.congreso.pases.events.PassAdquiredEvent;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PassServiceImpl implements PassService {

    private final ParticipantRepository participantRepository;
    private final PassRepository passRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random();

    @Override
    public Pass createPass(Pass pass) {
        log.debug("Creating Pass for Participant: {}", pass.getParticipantId());

        Participant passParticipant = participantRepository.findById(pass.getParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + pass.getParticipantId()));

        // Establecer estado inicial
        pass.setStatus(Pass.PassStatus.ACTIVE);

        if (pass.getPointsBalance() == null) {
            pass.setPointsBalance(0);
        }

        if (pass.getPointsBalance().compareTo(0) < 0) {
            throw new IllegalArgumentException("Initial balance of points cannot be negative");
        }

        Pass savedPass = passRepository.save(pass);
        log.info("Pass created successfully: {}", savedPass.getPassId());


        PassAdquiredEvent event = new PassAdquiredEvent();
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

        existingPass.setPassType(pass.getPassType());

        Pass updatedPass = passRepository.save(existingPass);
        log.info("Pass updated successfully: {}", id);
        return updatedPass;
    }

    @Override
    public void deletePass(Long id) {
        log.debug("Deleting Pass with id: {}", id);

        Pass pass = getPassById(id);

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
    public void startExchange(Long passId, String freebieId) {
        eventPublisher.publishEvent(new ExchangeRequestedEvent(passId, freebieId));
    }

}
