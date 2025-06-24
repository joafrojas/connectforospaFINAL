package com.example.SoporteTecnico.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motivo_soporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivoSoporte {

    @Schema(description = "ID del motivo de soporte técnico", example = "1")
    @Id
    private Long id_motivo_soporte;

    @Schema(description = "Nombre del motivo de soporte técnico", example = "Problema de conexión a internet")
    @Column()
    @JsonProperty("nombre_motivo") 
    private String nombreMotivo;
}
