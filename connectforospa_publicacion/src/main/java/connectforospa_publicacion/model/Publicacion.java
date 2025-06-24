package connectforospa_publicacion.model;

import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de Publicación para la gestión de publicaciones en ConnectForo")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la publicación", example = "1")
    @Column(name = "id_publicacion")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Título de la publicación", example = "Título de ejemplo")
    private String titulo;

    @Schema(description = "Contenido de la publicación", example = "Este es el contenido de la publicación.")
    private String contenido;

    @Column(name = "fecha_publicacion")
    @Schema(description = "Fecha y hora de publicación", example = "2023-10-01T12:00:00")
    private LocalDateTime fechaPublicacion;

    @Column(name = "id_foro", nullable = false)
    @Schema(description = "ID del foro al que pertenece la publicación", example = "1")
    private Long idForo;

    @Column(name = "id_categoria", nullable = false)
    @Schema(description = "ID de la categoría a la que pertenece la publicación", example = "1")
    private Long idCategoria;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que creó la publicación", example = "1")
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Schema(description = "Estado de la publicación", example = "PENDIENTE")
    private EstadoPublicacion estadoPublicacion;
}