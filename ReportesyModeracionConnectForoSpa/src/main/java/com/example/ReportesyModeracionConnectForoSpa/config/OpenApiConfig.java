package com.example.ReportesyModeracionConnectForoSpa.config;

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
                .title("Microservicio Reportes y Moderación ConnectForo SPA")
                .description("Documentación del microservicio de Reportes y Moderación")
                .version("1.0.0")
            );
    }
}
