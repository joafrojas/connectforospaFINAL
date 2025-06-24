package com.example.InteraccionesSociales2.webClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.InteraccionesSociales2.webClient.dto.Usuario;
import reactor.core.publisher.Mono;

@Component
public class UsuariosClient {

    private final WebClient webClient;

    public UsuariosClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8081/api/v1/usuarios")
                .build();
    }

    public Mono<Usuario> obtenerUsuarioPorId(Long id, String token) {
        return webClient.get()
                .uri("/{id}", id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Usuario.class);
    }
}


