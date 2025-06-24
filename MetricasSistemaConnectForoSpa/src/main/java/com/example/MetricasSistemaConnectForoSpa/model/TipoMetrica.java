package com.example.MetricasSistemaConnectForoSpa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;


@Schema(description = "Tipo o categoría de métrica del sistema")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoMetrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del tipo de métrica")
    private Long id_tipo;

    @Schema(description = "Nombre del tipo de métrica")
    private String nombre;
}
