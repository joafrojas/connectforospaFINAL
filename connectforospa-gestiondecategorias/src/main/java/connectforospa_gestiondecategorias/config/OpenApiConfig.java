package connectforospa_gestiondecategorias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionDeCategoriasApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Gestión de Categorías - ConnectForo")
                        .version("1.0")
                        .description("Documentación completa del microservicio de gestión de categorías, incluyendo operaciones CRUD y validaciones.")
                );
    }
}
