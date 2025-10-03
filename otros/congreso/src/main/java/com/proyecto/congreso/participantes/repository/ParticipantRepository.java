package com.proyecto.congreso.participantes.repository;

import com.proyecto.congreso.participantes.model.Participant;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepositoryImplementation<Participant, Long> {

    Optional<Participant> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Participant> findByStatus(Participant.ParticipantStatus status);

    List<Participant> findByNameContainingIgnoreCase(String name);
}
