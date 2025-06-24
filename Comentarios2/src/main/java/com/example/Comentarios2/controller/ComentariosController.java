package com.example.Comentarios2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Comentarios2.model.Comentarios;
import com.example.Comentarios2.service.ComentariosService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/microservicios/comentarios")

public class ComentariosController {

    private final ComentariosService comentariosService;

    @Autowired
    public ComentariosController(ComentariosService comentariosService) {
        this.comentariosService = comentariosService;
    }

    @Operation(summary = "Listar todos los comentarios", 
               description = "Devuelve una lista de todos los comentarios almacenados en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comentarios encontrada",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Comentarios.class))),
        @ApiResponse(responseCode = "204", description = "No hay comentarios registrados")
    })
    @GetMapping
    public ResponseEntity<List<Comentarios>> getAllComentarios() {
        List<Comentarios> lista2 = comentariosService.getAll();
        return lista2.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista2);
    }

    @Operation(summary = "Crear un nuevo comentario", 
               description = "Guarda un nuevo comentario asociado a un usuario, publicación y foro.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comentario creado correctamente",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Comentarios.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida por datos faltantes o incorrectos"),
        @ApiResponse(responseCode = "401", description = "Token no presente o no autorizado"),
        @ApiResponse(responseCode = "404", description = "Estado del comentario no válido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Comentarios> save(@RequestBody Comentarios comentarios) {
        try {
            Comentarios guardado = comentariosService.save(comentarios);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Buscar comentarios por usuario", 
               description = "Obtiene todos los comentarios realizados por un usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comentarios encontrados para el usuario",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Comentarios.class))),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene comentarios registrados")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Comentarios>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        List<Comentarios> comentarios = comentariosService.obtenerComentarioPorUsuario(idUsuario);
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Buscar comentarios por publicación", 
               description = "Obtiene todos los comentarios asociados a una publicación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comentarios encontrados para la publicación",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Comentarios.class))),
        @ApiResponse(responseCode = "204", description = "La publicación no tiene comentarios registrados")
    })
    @GetMapping("/publicacion/{idPublicacion}")
    public ResponseEntity<List<Comentarios>> obtenerComentarioPorPublicacion(@PathVariable Long idPublicacion) {
        List<Comentarios> comentarios = comentariosService.obtenerComentarioPorPublicacion(idPublicacion);
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Eliminar un comentario por ID", 
               description = "Elimina un comentario existente identificado por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comentario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario con el ID proporcionado")
    })
    @DeleteMapping("/{idComentario}")
    public ResponseEntity<Void> deletePorId(@PathVariable Long idComentario) {
        try {
            comentariosService.deletePorId(idComentario);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
