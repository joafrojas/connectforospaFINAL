package connectforospa_publicacion.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ConnectForoSPA API")
                        .version("1.0")
                        .description("API para la gestión de foros y publicaciones en ConnectForoSPA"));
    }


}
