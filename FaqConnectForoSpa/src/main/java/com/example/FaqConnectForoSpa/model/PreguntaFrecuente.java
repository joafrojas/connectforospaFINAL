package com.example.FaqConnectForoSpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "preguntas_frecuentes_spa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una pregunta frecuente con su respuesta.")
public class PreguntaFrecuente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la pregunta frecuente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Texto de la pregunta frecuente", example = "¿Cómo me registro en la plataforma?", required = true)
    private String pregunta;

    @Schema(description = "Respuesta correspondiente a la pregunta", example = "Para registrarte, haz clic en el botón de registro y completa el formulario.", required = true)
    private String respuesta;
}
