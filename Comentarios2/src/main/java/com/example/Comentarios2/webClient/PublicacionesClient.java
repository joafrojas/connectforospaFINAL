package com.example.Comentarios2.webClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.Comentarios2.webClient.dto.Publicacion;
import reactor.core.publisher.Mono;

@Component
public class PublicacionesClient {
    private final WebClient webClient;

    public PublicacionesClient(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://localhost:8083/api/v1/publicaciones")
            .build();
    }

    public Mono<Publicacion> obtenerPublicacionPorId(Long id) {
        return webClient.get()
            .uri("/{id}", id)
            .retrieve()
            .bodyToMono(Publicacion.class);
    }
}