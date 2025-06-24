package com.example.SoporteTecnico.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "soporteTecnico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoporteTecnico {

    @Schema(description = "ID del soporte técnico", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSoporte;

    @Schema(description = "ID del usuario que solicita el soporte técnico", example = "1")
    @JoinColumn(name = "id_usuario")
    private Long idUsuario;

    @Schema(description = "Motivo del soporte técnico", example = "Problema de conexión a internet")
    @ManyToOne
    @JoinColumn(name = "id_motivo_soporte", nullable = false)
    private MotivoSoporte motivo;

    @Schema(description = "Descripción del soporte técnico", example = "No puedo conectarme a internet desde mi ordenador")
    @Column(nullable = false)
    private String descripcion;

    @Schema(description = "Fecha y hora de la solicitud de soporte técnico", example = "2023-10-01T12:00:00")
    @Column()
    private LocalDateTime fechaSoporte = LocalDateTime.now();

    @Schema(description = "Estado del soporte técnico", example = "Pendiente")
    @Column(nullable = false)
    private String estado;
}
