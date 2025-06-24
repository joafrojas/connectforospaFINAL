package com.example.Comentarios2.model;

import java.time.LocalDateTime;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un comentario realizado por un usuario en una publicación dentro de un foro.")
public class Comentarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del comentario", example = "1")
    private Long idComentario;

    @Column
    @Schema(description = "Fecha y hora en que se publicó el comentario", example = "2025-06-23T18:45:00")
    private LocalDateTime fechaPublicacion;

    @Column(nullable = false)
    @Schema(description = "Contenido textual del comentario", example = "Este es un comentario de prueba.")
    private String contenido;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que escribió el comentario", example = "12")
    private Long idUsuario;

    @Column(name = "id_publicacion", nullable = false)
    @Schema(description = "ID de la publicación sobre la que se comenta", example = "45")
    private Long idPublicacion;

    @ManyToOne
    @JoinColumn(name = "id_estado_comentario", nullable = false)
    @Schema(description = "Estado actual del comentario (ej. ACTIVO, ELIMINADO, OCULTO)")
    private EstadoComentario estadoComentario;

    @Column(name = "id_foro", nullable = false)
    @Schema(description = "ID del foro al que pertenece la publicación comentada", example = "7")
    private Long idForo;
}
