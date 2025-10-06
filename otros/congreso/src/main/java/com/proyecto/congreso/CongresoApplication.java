package com.proyecto.congreso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;

@Modulithic
@SpringBootApplication
@EnableJpaRepositories
@EnableMongoRepositories
@EnableAsync
public class CongresoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongresoApplication.class, args);
	}

}
