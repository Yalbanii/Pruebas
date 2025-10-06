package com.proyecto.congreso.pases.repository;

import com.proyecto.congreso.pases.model.Pass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassRepository extends JpaRepository<Pass, Long> {

    // Verificar si existe un número de Pase
    boolean existsByPassId(Long passId);

    // Buscar inscripciones por participantId
    List<Pass> findByParticipantId(Long participantId);

    // Buscar Pases por estado
    List<Pass> findByStatus(Pass.PassStatus status);

    // Buscar Pases por tipo
    List<Pass> findByPassType(Pass.PassType passType);

    // Buscar Pases activos de un participante
    @Query("SELECT a FROM Pass a WHERE a.participantId = :participantId AND a.status = 'ACTIVE'")
    List<Pass> findActivePassByParticipantId(@Param("participantId") Long participantId);

    // Contar Pases de un participante
    long countByParticipantId(Long participantId);

    // Buscar cuentas activas (para batch processing)
    @Query("SELECT a FROM Pass a WHERE a.status = 'ACTIVE'")
    List<Pass> findActivePass();

    List<Pass> findByPassId(Long passId);

    Optional<Long> findParticipantIdByPassId(Long passId);

}
