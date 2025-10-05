package com.proyecto.congreso.pases.repository;

import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.model.Pass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // Verificar si existe
    boolean existsByPassId(Long passId);

    List<Pass> findByPassId(Long participantId);
}
