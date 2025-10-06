package com.proyecto.congreso.points.assistance.repository;

import com.proyecto.congreso.points.assistance.model.Asistencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AsistenciaRepository  extends MongoRepository<Asistencia, String> {

    List<Asistencia> findByPassId(Long passId);

    // Buscar asistentes a una conferencia específica
    List<Asistencia> findByConferenciaId(String conferenciaId);

    // Verificar si ya existe asistencia (evitar duplicados)
    boolean existsByPassIdAndConferenciaId(Long passId, String conferenciaId);

    // Obtener asistencias por participante
    List<Asistencia> findByParticipantId(Long participantId);

    // Obtener asistencias en un rango de fechas
    List<Asistencia> findByFechaAsistenciaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Contar asistencias de un Pass
    long countByPassId(Long passId);

    // Buscar por Pass y estado
    List<Asistencia> findByPassIdAndStatus(Long passId, String status);

    List<Asistencia> findByStatus(String status);

    // Calcular total de puntos acumulados por Pass
    @Query("{ 'passId': ?0, 'status': 'PROCESADA' }")
    List<Asistencia> findAsistenciasProcedasByPass(Long passId);
}
