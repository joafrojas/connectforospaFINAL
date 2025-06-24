package com.example.AnuncioConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "estado_anuncio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estado posible para un anuncio")
public class Estado {

    @Id
    @Column(name = "id_estado")
    @Schema(description = "ID único del estado")
    private Long idEstado;

    @Schema(description = "Nombre del estado")
    private String nombre;
}
