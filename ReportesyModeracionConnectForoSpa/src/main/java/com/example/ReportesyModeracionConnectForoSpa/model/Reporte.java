package com.example.ReportesyModeracionConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte de usuario por alguna razón, con estado y fechas")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del reporte", example = "100", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Motivo del reporte", example = "Contenido ofensivo", required = true)
    private String motivo;

    @Schema(description = "Respuesta o comentarios del moderador", example = "Revisado y sancionado", required = false)
    private String respuesta;

    @Schema(description = "Fecha en que se creó el reporte", example = "2025-06-22T15:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fecha;

    @Column(name = "id_usuario_generador")
    @Schema(description = "ID del usuario que genera el reporte", example = "12345", required = true)
    private Long idUsuarioGenerador;

    @Column(name = "id_usuario_reportado")
    @Schema(description = "ID del usuario reportado", example = "67890", required = true)
    private Long idUsuarioReportado;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    @Schema(description = "Estado actual del reporte")
    private EstadoReporte estadoReporte;
}
