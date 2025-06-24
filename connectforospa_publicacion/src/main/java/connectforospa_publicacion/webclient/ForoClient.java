package connectforospa_publicacion.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ForoClient {

    private final WebClient webClient;

    public ForoClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8082/api/v1/foros") 
                .build();
    }

    public Mono<Boolean> validarForo(Long idForo) {
        return webClient.get()
                .uri("/{id}", idForo)
                .retrieve()
                .toBodilessEntity()
                .map(respuesta -> respuesta.getStatusCode().is2xxSuccessful())
                .onErrorResume(error -> {
                    log.error("Error validando foro {}: {}", idForo, error.getMessage());
                    return Mono.just(false);
                });
    }
}