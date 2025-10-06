package com.proyecto.congreso.pases.repository;

import com.proyecto.congreso.pases.model.SpecialAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialAccessRepository extends JpaRepository<SpecialAccess, Long> {

    boolean existsByPassId(Long passId);
}
