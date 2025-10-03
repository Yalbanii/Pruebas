package com.proyecto.congreso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.modulith.Modulithic;

@Modulithic
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.proyecto.congreso.participantes.repository",
        "com.proyecto.congreso.pases.repository"
})
@EnableMongoRepositories
public class CongresoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongresoApplication.class, args);
	}

}
