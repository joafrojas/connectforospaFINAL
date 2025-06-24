package connectforospa_publicacion.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de una publicación")
public enum EstadoPublicacion {
    @Schema(description = "Publicación pendiente de revisión")
    PENDIENTE,
    @Schema(description = "Publicación aprobada")
    APROBADA,
    @Schema(description = "Publicación rechazada")
    RECHAZADA
}
