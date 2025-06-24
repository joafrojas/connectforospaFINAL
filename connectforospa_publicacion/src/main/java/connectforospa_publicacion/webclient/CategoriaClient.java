package connectforospa_publicacion.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CategoriaClient {

    private final WebClient webClient;

    public CategoriaClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084").build(); 
    }

    public Mono<Boolean> validarCategoria(Long idCategoria) {
        return webClient.get()
                .uri("/api/v1/categorias/" + idCategoria)
                .retrieve()
                .bodyToMono(CategoriaDTO.class) 
                .map(categoria -> categoria != null) 
                .onErrorResume(error -> {
                    log.error("Error consultando categoría: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}