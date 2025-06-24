package com.example.AnuncioConnectForoSpa.webclient;

import com.example.AnuncioConnectForoSpa.webclient.dto.RolResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8081/api/v1/usuarios")
                .build();
    }

    public Mono<Boolean> tieneRolAnuncios(Long idUsuario) {
        return webClient.get()
                .uri("/{id}/roles", idUsuario)
                .retrieve()
                .bodyToFlux(RolResponse.class)
                .any(rol -> "ROLE_ANUNCIOS".equalsIgnoreCase(rol.getNombreRol()));
    }
}