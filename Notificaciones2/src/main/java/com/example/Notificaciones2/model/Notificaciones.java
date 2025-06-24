package com.example.Notificaciones2.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificaciones {

    
    @Id
    @Schema(description = "ID de la notificación", example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @Schema(description = "ID del usuario al que se envía la notificación", example = "1")
    @JoinColumn(name = "id_usuario")
    private Long idUsuario;


    @Schema(description = "Tipo de notificación", example = "Mensaje")  
    @Column(nullable = false)
    private String mensaje;


    @Schema(description = "Descripción de la notificación", example = "Tienes un nuevo mensaje en tu bandeja de entrada")
    @Column(nullable = false)
    private String estado;

    @Schema(description = "Fecha y hora de envío de la notificación", example = "2023-10-01T12:00:00")
    @Column()
    private LocalDateTime fechaEnvio = LocalDateTime.now();
}
