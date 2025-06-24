package com.example.ReportesyModeracionConnectForoSpa.webclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// DTO auxiliar para recibir datos del usuario
@Data
@NoArgsConstructor
@AllArgsConstructor
class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String email;
    private String estado;
    private String rol;
}

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://localhost:8081/api/v1/usuarios") // cambia rurl
            .build();
    }

    public Mono<Boolean> validarUsuarioActivo(Long idUsuario) {
        return webClient.get()
            .uri("/{id}", idUsuario)
            .retrieve()
            .bodyToMono(UsuarioDTO.class)
            .map(usuario -> usuario.getEstado().equalsIgnoreCase("ACTIVO"))
            .onErrorReturn(false); 
    }

    public Mono<UsuarioDTO> obtenerUsuarioPorId(Long idUsuario) {
        return webClient.get()
            .uri("/{id}", idUsuario)
            .retrieve()
            .bodyToMono(UsuarioDTO.class)
            .onErrorResume(e -> Mono.empty());
    }
}

