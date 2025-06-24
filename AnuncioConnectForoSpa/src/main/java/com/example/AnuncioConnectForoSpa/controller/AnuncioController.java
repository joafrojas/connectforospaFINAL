package com.example.AnuncioConnectForoSpa.controller;

import com.example.AnuncioConnectForoSpa.dto.AnuncioRequest;
import com.example.AnuncioConnectForoSpa.dto.AnuncioResponse;
import com.example.AnuncioConnectForoSpa.model.Anuncio;
import com.example.AnuncioConnectForoSpa.service.AnuncioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/anuncios")
public class AnuncioController {

    @Autowired
    private AnuncioService service;

    @Operation(summary = "Obtener todos los anuncios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de anuncios obtenida correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnuncioResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<AnuncioResponse>> getAll() {
        List<AnuncioResponse> lista = service.getAll();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Crear un nuevo anuncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Anuncio creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Anuncio.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AnuncioRequest request) {
        try {
            Anuncio creado = service.create(request); // Cambiado de save a create
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }

    @Operation(summary = "Actualizar un anuncio completo por ID")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del anuncio a actualizar", required = true) @PathVariable Long id,
            @Parameter(description = "Datos para actualizar el anuncio", required = true) @RequestBody AnuncioRequest request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @Operation(summary = "Actualizar solo la prioridad de un anuncio")
    @PatchMapping("/{id}/prioridad")
    public ResponseEntity<?> actualizarPrioridad(
            @Parameter(description = "ID del anuncio", required = true) @PathVariable Long id,
            @Parameter(description = "Nueva prioridad del anuncio", required = true) @RequestParam String nuevaPrioridad) {
        try {
            return ResponseEntity.ok(service.updatePrioridad(id, nuevaPrioridad));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @Operation(summary = "Eliminar un anuncio por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @Parameter(description = "ID del anuncio a eliminar", required = true) @PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @Operation(summary = "Obtener anuncios recientes desde una fecha dada")
    @GetMapping("/recientes")
    public ResponseEntity<List<Anuncio>> recientes(
            @Parameter(description = "Fecha desde la cual obtener anuncios (ISO-8601)", required = true) @RequestParam String desde) {
        List<Anuncio> lista = service.getByFecha(LocalDateTime.parse(desde));
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener anuncios filtrados por prioridad")
    @GetMapping("/prioridad")
    public ResponseEntity<List<Anuncio>> porPrioridad(
            @Parameter(description = "Valor de prioridad para filtrar", required = true) @RequestParam String valor) {
        List<Anuncio> lista = service.getPorPrioridad(valor);
        return ResponseEntity.ok(lista);
    }
}
