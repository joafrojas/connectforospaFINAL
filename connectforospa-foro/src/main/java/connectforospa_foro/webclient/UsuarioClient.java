package connectforospa_foro.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    public UsuarioDTO obtenerUsuario(Long idUsuario) {
        try {
            UsuarioDTO usuario = webClient.get()
                    .uri("/api/v1/usuarios/{id}", idUsuario)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block(); //
            log.info("Usuario recibido: {}", usuario);
            return usuario;
        } catch (Exception e) {
            log.error("Error al obtener usuario {}: {}", idUsuario, e.getMessage());
            return null;
        }
    }
}