package connectforospa_gestiondecategorias.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsuarioClient {

    private final WebClient webClient = WebClient.create("http://localhost:8081/api/v1/usuarios");

    public Mono<UsuarioDTO> obtenerUsuario(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                          response -> response.createException().flatMap(Mono::error))
                .bodyToMono(UsuarioDTO.class)
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }
}