package com.proyecto.congreso.points.exchange.repository;

import com.proyecto.congreso.points.exchange.model.Exchange;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends MongoRepository<Exchange, String> {


    // Buscar todas los Exchanges de un Pass
    List<Exchange> findByPassId(Long passId);

    // Buscar asistentes a una conferencia específica
    List<Exchange> findByFreebieId(String freebieId);

    // Verificar si ya existe Exchange (evitar duplicados)
    boolean existsByPassIdAndFreebieId(Long passId, String freebieId);

    // Obtener Exchanges por participante
    List<Exchange> findByParticipantId(Long participantId);

    // Contar Exchanges de un Pass
    long countByPassId(Long passId);

    // Calcular total de puntos reducidos por Pass
    @Query("{ 'passId': ?0 }")
    List<Exchange> findExchangesProcedosByPass(Long passId);
}
