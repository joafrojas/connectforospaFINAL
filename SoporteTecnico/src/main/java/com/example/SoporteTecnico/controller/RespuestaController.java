package com.example.SoporteTecnico.controller;

import com.example.SoporteTecnico.model.Respuesta;
import com.example.SoporteTecnico.service.RespuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    private final RespuestaService respuestaService;

    public RespuestaController(RespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @Operation(summary = "Crear una nueva respuesta para un soporte técnico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Respuesta creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Respuesta.class))),
        @ApiResponse(responseCode = "400", description = "Descripción vacía"),
        @ApiResponse(responseCode = "404", description = "Soporte técnico no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{idSoporte}")
    public ResponseEntity<?> crearRespuesta(@PathVariable Long idSoporte, @RequestBody String descripcion) {
        try {
            Respuesta respuesta = respuestaService.crearRespuesta(idSoporte, descripcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + ex.getMessage());
        }
    }

    @Operation(summary = "Obtener todas las respuestas asociadas a un soporte técnico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuestas encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Respuesta.class))),
        @ApiResponse(responseCode = "204", description = "No hay respuestas disponibles"),
        @ApiResponse(responseCode = "404", description = "Soporte técnico no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/soporte/{id}")
    public ResponseEntity<?> obtenerRespuestasPorSoporte(@PathVariable Long id) {
        try {
            List<Respuesta> lista = respuestaService.obtenerRespuestasPorSoporte(id);

            if (lista.isEmpty()) {
                return ResponseEntity.noContent().build(); // Retorna 204 sin cuerpo
            }

            return ResponseEntity.ok(lista); // Retorna 200 con la lista
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error inesperado: " + ex.getMessage());
        }
    }

}