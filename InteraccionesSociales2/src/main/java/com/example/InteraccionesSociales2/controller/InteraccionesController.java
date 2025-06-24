package com.example.InteraccionesSociales2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.InteraccionesSociales2.model.Interacciones;
import com.example.InteraccionesSociales2.service.InteraccionesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/interacciones")


public class InteraccionesController {

    private final InteraccionesService interaccionesService;

    public InteraccionesController(InteraccionesService interaccionesService) {
        this.interaccionesService = interaccionesService;
    }

    @Operation(summary = "Obtener todas las interacciones registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interacciones encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Interacciones.class))),
        @ApiResponse(responseCode = "204", description = "No hay interacciones disponibles")
    })
    @GetMapping
    public ResponseEntity<List<Interacciones>> getAllInteracciones() {
        List<Interacciones> lista = interaccionesService.getAll();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Guardar una nueva interacción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Interacción guardada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Interacciones.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada"),
        @ApiResponse(responseCode = "409", description = "Conflicto de interacción"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<String> guardar(
        @RequestBody(
            description = "Datos de la interacción a guardar",
            required = true,
            content = @Content(schema = @Schema(implementation = Interacciones.class))
        )
        @org.springframework.web.bind.annotation.RequestBody Interacciones interacciones
    ) {
        try {
            Interacciones guardada = interaccionesService.save(interacciones);
            return ResponseEntity.status(HttpStatus.CREATED).body("Interacción guardada con ID: " + guardada.getIdInteraccion());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
        }
    }

    @Operation(summary = "Obtener interacciones por ID de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interacciones del usuario encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Interacciones.class))),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene interacciones")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Interacciones>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        List<Interacciones> lista = interaccionesService.obtenerInteraccionesPorUsuario(idUsuario);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener interacción por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interacción encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Interacciones.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró la interacción")
    })
    @GetMapping("/{idInteraccion}")
    public ResponseEntity<Interacciones> obtenerInteraccionPorId(@PathVariable Long idInteraccion) {
        Interacciones resultado = interaccionesService.obtenerInteraccionPorId(idInteraccion);
        return (resultado == null) 
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(resultado);
    }


    @Operation(summary = "Obtener interacciones por ID de publicación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interacciones encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Interacciones.class))),
        @ApiResponse(responseCode = "204", description = "No hay interacciones para la publicación")
    })
    @GetMapping("/publicacion/{idPublicacion}")
    public ResponseEntity<List<Interacciones>> obtenerPorPublicacion(@PathVariable Long idPublicacion) {
        List<Interacciones> lista = interaccionesService.obtenerInteraccionPorPublicacion(idPublicacion);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Eliminar una interacción por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Interacción eliminada"),
        @ApiResponse(responseCode = "404", description = "Interacción no encontrada")
    })
    @DeleteMapping("/{idInteraccion}")
    public ResponseEntity<String> deletePorId(@PathVariable Long idInteraccion) {
        try {
            interaccionesService.deletePorId(idInteraccion);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}