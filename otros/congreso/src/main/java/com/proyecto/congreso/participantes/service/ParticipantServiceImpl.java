package com.proyecto.congreso.participantes.service;

import com.proyecto.congreso.participantes.events.ParticipantCreatedEvent;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

private final ParticipantRepository participantRepository;
private final ApplicationEventPublisher eventPublisher;

@Override
public Participant createParticipant(Participant participant) {
    log.debug("Creating participant with email: {}", participant.getEmail());

    if (participantRepository.existsByEmail(participant.getEmail())) {
        throw new IllegalArgumentException("Email already exists: " + participant.getEmail());
    }

    participant.setStatus(Participant.ParticipantStatus.ACTIVE);
    Participant savedParticipant = participantRepository.save(participant);
    log.info("Participant created successfully with id: {}", savedParticipant.getParticipantId());

    // Publicar evento
    ParticipantCreatedEvent event = new ParticipantCreatedEvent(
            savedParticipant.getParticipantId(),
            savedParticipant.getName(),
            savedParticipant.getEmail(),
            LocalDateTime.now()
    );

    eventPublisher.publishEvent(event);
    log.debug("ParticipantCreatedEvent published for Participant: {}", savedParticipant.getEmail());

    return savedParticipant;
}

@Override
public Participant updateParticipant(Long id, Participant participant) {
    log.debug("Updating Participant with id: {}", id);

    Participant existingParticipant = getParticipantById(id);

    // Verificar si el email cambió y si ya existe
    if (!existingParticipant.getEmail().equals(participant.getEmail()) &&
            participantRepository.existsByEmail(participant.getEmail())) {
        throw new IllegalArgumentException("Email already exists: " + participant.getEmail());
    }

    existingParticipant.setName(participant.getName());
    existingParticipant.setLastName(participant.getLastName());
    existingParticipant.setEmail(participant.getEmail());
    existingParticipant.setPhone(participant.getPhone());
    existingParticipant.setNacionality(participant.getNacionality());
    existingParticipant.setAge(participant.getAge());
    existingParticipant.setArea(participant.getArea());

    Participant updatedParticipant = participantRepository.save(existingParticipant);
    log.info("Participant updated successfully with id: {}", updatedParticipant.getParticipantId());
    return updatedParticipant;
}

@Override
@Transactional(readOnly = true)
public Participant getParticipantById(Long id) {
    log.debug("Getting Participant by id: {}", id);
    return participantRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + id));
}

@Override
@Transactional(readOnly = true)
public List<Participant> getAllParticipants() {
    log.debug("Getting all Participants");
    return participantRepository.findAll();
}

@Override
@Transactional(readOnly = true)
public List<Participant> getParticipantByStatus(Participant.ParticipantStatus status) {
    log.debug("Getting Participants by status: {}", status);
    return participantRepository.findByStatus(status);
}

@Override
public void deleteParticipant(Long id) {
    log.debug("Deleting Participant with id: {}", id);

    Participant participant = getParticipantById(id);
    participant.setStatus(Participant.ParticipantStatus.INACTIVE);
    participantRepository.save(participant);
    log.info("Participant soft deleted (deactivated) with id: {}", id);
}

@Override
public Participant activateParticipant(Long id) {
    log.debug("Activating Participant with id: {}", id);

    Participant participant = getParticipantById(id);
    participant.setStatus(Participant.ParticipantStatus.ACTIVE);
    Participant activatedParticipant = participantRepository.save(participant);
    log.info("Participant activated successfully with id: {}", id);
    return activatedParticipant;
}

@Override
public Participant deactivateParticipant(Long id) {
    log.debug("Deactivating Participant with id: {}", id);

    Participant participant = getParticipantById(id);
    participant.setStatus(Participant.ParticipantStatus.INACTIVE);
    Participant deactivatedParticipant = participantRepository.save(participant);
    log.info("Participant deactivated successfully with id: {}", id);
    return deactivatedParticipant;
}

@Override
@Transactional(readOnly = true)
public boolean existsByEmail(String email) {
    return participantRepository.existsByEmail(email);
}
}

