package com.example.AnuncioConnectForoSpa.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioResponse {
    private Long id_anuncio;
    private String titulo;
    private LocalDateTime fecha_publicacion;
    private String prioridad;
    private String nombreEstado;
    private String nombreUsuario;
}