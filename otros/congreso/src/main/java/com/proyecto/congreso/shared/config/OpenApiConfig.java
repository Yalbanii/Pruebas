package com.proyecto.congreso.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Congreso Sistema Interactivo API")
                        .version("1.0")
                        .description("API REST para el Congreso Sistema Interactivo")
                        .contact(new Contact()
                                .name("Congreso")
                                .email("support@congreso.com")));
    }


    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.proyecto.congreso.pases.controller", "com.proyecto.congreso.participantes.controller" )
                .build();
    }
}
