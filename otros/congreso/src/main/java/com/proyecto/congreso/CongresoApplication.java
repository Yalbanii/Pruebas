package com.proyecto.congreso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal del Sistema de Congreso Interactivo.
 *
 * Anotaciones principales:
 * - @Modulithic: Habilita Spring Modulith para arquitectura modular
 * - @EnableAsync: Permite procesamiento asíncrono de eventos
 * - @EnableJpaRepositories: Configuración de repositorios JPA (MySQL)
 * - @EnableMongoRepositories: Configuración de repositorios MongoDB
 */

@Modulithic
@SpringBootApplication
@EnableJpaRepositories
@EnableMongoRepositories
@EnableAsync  // Necesario para @Async en event listeners
public class CongresoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongresoApplication.class, args);
	}

}
