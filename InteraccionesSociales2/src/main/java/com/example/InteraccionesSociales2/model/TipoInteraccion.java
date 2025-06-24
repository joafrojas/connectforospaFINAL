package com.example.InteraccionesSociales2.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_interaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoInteraccion {

    @Id
    @Column(name = "id_tipo", nullable = false)
    @Schema(description = "ID del tipo de interacción", example = "1")
    private Long id_tipo;

    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre del tipo de interacción", example = "Me gusta")
    private String nombre;
}