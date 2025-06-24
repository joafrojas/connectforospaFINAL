package com.example.AnuncioConnectForoSpa.dto;

import lombok.Data;

@Data
public class AnuncioRequest {
    private String titulo;
    private String foto;
    private String prioridad;
    private Long idUsuario;
        private Long idEstado;
    }