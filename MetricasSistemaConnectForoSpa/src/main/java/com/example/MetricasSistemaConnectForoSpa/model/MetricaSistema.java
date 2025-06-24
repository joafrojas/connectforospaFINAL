package com.example.MetricasSistemaConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una métrica del sistema") // Usar en DTO
public class MetricaSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la métrica")
    private Long id_metrica;

    @Schema(description = "Descripción de la métrica")
    private String descripcion;

    //@Schema(description = "Fecha y hora del registro de la métrica")
    private LocalDateTime fecha_registro;

    @ManyToOne
    @JoinColumn(name = "id_tipo")
    @Schema(description = "Tipo de métrica asociado")
    private TipoMetrica tipo;
}
