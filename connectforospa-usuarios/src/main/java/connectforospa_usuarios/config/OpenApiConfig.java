package connectforospa_usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usuariosApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("TokenAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("TokenAuth"))
                .info(new Info()
                        .title("Microservicio de Usuarios - ConnectForo")
                        .version("1.0")
                        .description(
                                "Documentación completa del microservicio de usuarios, roles, validaciones y seguridad por token."));
    }

   
}