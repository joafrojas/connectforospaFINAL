package com.example.AnuncioConnectForoSpa.config;

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
                .title("Microservicio Anuncios ConnectForo SPA")
                .description("Documentación del microservicio de Anuncios")
                .version("1.0.0")
            );
    }
}
