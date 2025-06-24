package com.example.SoporteTecnico.model;


import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Respuesta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Respuesta {

    @Schema(description = "ID de la respuesta", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRespuesta;

    @Schema(description = "Soporte técnico asociado a la respuesta", example = "1")
    @OneToOne
    @JoinColumn(name = "id_soporte")
    private SoporteTecnico idSoporteTecnico;

    @Schema(description = "Fecha y hora de la respuesta", example = "2023-10-01T12:00:00")
    @Column()
    private LocalDateTime fechaRespuesta = LocalDateTime.now();

    @Schema(description = "Descripción de la respuesta", example = "La incidencia ha sido resuelta satisfactoriamente.")
    @Column(nullable = false, length = 1000)
    private String descripcion;

}
