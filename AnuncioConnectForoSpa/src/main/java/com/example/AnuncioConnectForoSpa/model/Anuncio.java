package com.example.AnuncioConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un anuncio")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anuncio")
    @Schema(description = "ID único del anuncio")
    private Long idAnuncio;

    @Schema(description = "Título del anuncio")
    private String titulo;

    @Schema(description = "Foto del anuncio")
    private String foto;

    @Column(name = "fecha_publicacion")
    @Schema(description = "Fecha y hora de publicación del anuncio")
    private LocalDateTime fechaPublicacion;

    @Schema(description = "Prioridad del anuncio")
    private String prioridad;

    @Column(name = "usuario_id")
    @Schema(description = "ID del usuario que creó el anuncio")
    private Long usuarioId;

    @ManyToOne
    @JoinColumn(name = "estado_id_estado", referencedColumnName = "id_estado")
    @Schema(description = "Estado actual del anuncio")
    private Estado estado;
}
