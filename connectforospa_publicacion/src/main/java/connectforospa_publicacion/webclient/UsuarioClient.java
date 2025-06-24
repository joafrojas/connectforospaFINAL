package connectforospa_publicacion.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    public Mono<UsuarioDTO> obtenerUsuario(Long idUsuario) {
        return webClient.get()
                .uri("/api/v1/usuarios/" + idUsuario) 
                .retrieve()
                .bodyToMono(UsuarioDTO.class)
                .onErrorResume(error -> {
                    log.error("Error conectando con el servicio de usuarios: {}", error.getMessage());
                    return Mono.empty(); 
                });
    }
    
}
