package com.example.InteraccionesSociales2.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interacciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la interacción", example = "1")
    private Long idInteraccion;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    @Schema(description = "Tipo de interacción realizada")
    private TipoInteraccion tipoInteraccion;

    @Schema(description = "Fecha en que se registró la interacción", example = "2025-06-23T18:15:00")
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @Schema(description = "ID del usuario que realiza la interacción", example = "1")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Schema(description = "ID de la publicación asociada", example = "2")
    @Column(name = "id_publicacion", nullable = false)
    private Long idPublicacion;

    @Schema(description = "ID del foro asociado", example = "1")
    @Column(name = "id_foro", nullable = false)
    private Long idForo;
}