package com.example.Comentarios2.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoComentario {

    @Id
    @Schema(description = "ID del estado del comentario", example = "1")
    private Long idEstado;

    @Schema(description = "Nombre del estado del comentario", example = "Activo")
    @Column(name = "nombre_estado", length = 40)
    private String nombreEstado;
}
