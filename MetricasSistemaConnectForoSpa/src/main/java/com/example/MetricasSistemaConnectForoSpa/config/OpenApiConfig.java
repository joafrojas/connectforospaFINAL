package com.example.MetricasSistemaConnectForoSpa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("Microservicio Métricas del Sistema")
                .description("Documentación del microservicio de Métricas del Sistema")
                .version("1.0.0")
            );
    }
}
