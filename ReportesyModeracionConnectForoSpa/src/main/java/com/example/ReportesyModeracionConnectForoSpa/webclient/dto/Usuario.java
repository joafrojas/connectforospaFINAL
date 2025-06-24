package com.example.ReportesyModeracionConnectForoSpa.webclient.dto;

import lombok.Data;

@Data
public class Usuario {
    private Long idUsuario;
    private Long idRol;
    private String correo;
    private String contraseña;
}
