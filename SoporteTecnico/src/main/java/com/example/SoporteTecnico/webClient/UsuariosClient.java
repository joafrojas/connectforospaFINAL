package com.example.SoporteTecnico.webClient;

import com.example.SoporteTecnico.webClient.dto.Usuario;
import com.example.SoporteTecnico.webClient.dto.RolResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class UsuariosClient {

    private final WebClient webClient;

    public UsuariosClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8081/api/v1/usuarios")
                .build();
    }

    public Mono<Usuario> obtenerUsuarioPorId(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Usuario.class);
    }

    public Flux<RolResponse> obtenerRolesPorUsuario(Long idUsuario) {
        return webClient.get()
                .uri("/{id}/roles", idUsuario)
                .retrieve()
                .bodyToFlux(RolResponse.class);
    }

    public Mono<Boolean> tieneRolSoporte(Long idUsuario) {
        return obtenerRolesPorUsuario(idUsuario)
                .any(rol -> "ROLE_SOPORTE".equalsIgnoreCase(rol.getNombreRol()));
    }
}