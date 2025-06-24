package com.example.AnuncioConnectForoSpa.webclient.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long idUsuario;
    private String nombreUsuario;
    private String email;
}
