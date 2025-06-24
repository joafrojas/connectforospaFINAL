package com.example.SoporteTecnico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpeenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Soporte Técnico API")
                        .version("1.0.0")
                        .description("API para gestionar el soporte técnico de la aplicación"));
    }

}
