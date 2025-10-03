package com.proyecto.congreso.participantes.service;

import com.proyecto.congreso.participantes.model.Participant;

import java.util.List;

public interface ParticipantService {

    Participant createParticipant(Participant customer);

    Participant updateParticipant(Long id, Participant customer);

    Participant getParticipantById(Long id);

    List<Participant> getAllParticipants();

    List<Participant> getParticipantByStatus(Participant.ParticipantStatus status);

    void deleteParticipant(Long id);

    Participant activateParticipant(Long id);

    Participant deactivateParticipant(Long id);

    boolean existsByEmail(String email);
}
