package com.example.AnuncioConnectForoSpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long idUsuario;
    private String email;
    private String nombreUsuario;
    private String estado;
    private String rol;
}

