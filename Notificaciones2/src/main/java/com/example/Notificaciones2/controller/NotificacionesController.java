package com.example.Notificaciones2.controller;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Notificaciones2.model.Notificaciones;
import com.example.Notificaciones2.service.NotificacionesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/microservicios/notificaciones")
public class NotificacionesController {

    private NotificacionesService notificacionesService;


    @Autowired
    public NotificacionesController(NotificacionesService notificacionesService){
        this.notificacionesService = notificacionesService;
    }


    @Operation(summary = "Obtener una notificación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notificaciones> obtenerPorId(@PathVariable Long id) {
        Notificaciones notificacion = notificacionesService.obtenerNotificacionPorId(id);
        return notificacion == null
            ? ResponseEntity.notFound().build()
            : ResponseEntity.ok(notificacion);
    }



    @Operation(summary = "Crear una nueva notificación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación guardada con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Notificaciones notificaciones) {
        try {
            Notificaciones guardada = notificacionesService.save(notificaciones);
            return ResponseEntity.ok(guardada);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    

    @Operation(summary = "Obtener todas las notificaciones de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "204", description = "No hay notificaciones para este usuario")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificaciones>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        List<Notificaciones> lista = notificacionesService.obtenerNotificacionPorUsuario(idUsuario);
        return lista.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(lista);
    }


    @Operation(summary = "Eliminar una notificación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificación eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionesService.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    @Operation(summary = "Obtener todas las notificaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de todas las notificaciones",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "204", description = "No hay notificaciones disponibles")
    })
    @GetMapping
    public ResponseEntity<List<Notificaciones>> obtenerTodas() {
        List<Notificaciones> lista = notificacionesService.getAll();
        return lista.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(lista);
    }

}
