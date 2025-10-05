package com.proyecto.congreso.points.calculator.repository;

import com.proyecto.congreso.points.calculator.model.Freebies;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebieRepository extends MongoRepository<Freebies, String> {
}
