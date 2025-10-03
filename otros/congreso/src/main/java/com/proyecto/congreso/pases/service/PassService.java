package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Pass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PassService {
    // CRUD operations
    Pass createPass(Pass pass);
    Pass getPassById(Long passId);
    List<Pass> getAllPass();
    List<Pass> getPassByParticipantId(Long participantId);
    List<Pass> getActivePassByParticipantId(Long participantId);
    Pass updatePass(Long passId, Pass pass);
    void deletePass(Long passId);

    // Pass Insription status operations
    Pass activatePass(Long passId);
    Pass closePass(Long passId);

    // Query operations
    List<Pass> getPassByStatus(Pass.PassStatus status);
    List<Pass> getPassByType(Pass.PassType passType);
    long countPassByParticipantId(Long participantId);
    boolean existsByPassId(Long passId);
}
