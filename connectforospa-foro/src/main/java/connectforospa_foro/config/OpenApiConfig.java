package connectforospa_foro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foroApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Foro - ConnectForo")
                        .version("1.0")
                        .description("Documentación completa del foro: publicaciones, comentarios, categorías y más.")
                );
    }
}