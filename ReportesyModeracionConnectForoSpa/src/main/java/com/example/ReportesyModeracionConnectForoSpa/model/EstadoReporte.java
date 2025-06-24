package com.example.ReportesyModeracionConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estado del reporte, por ejemplo PENDIENTE, RESUELTO, etc.")
public class EstadoReporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del estado", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del estado", example = "PENDIENTE", required = true)
    private String nombre;
}
