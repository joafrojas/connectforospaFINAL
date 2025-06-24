package com.example.SoporteTecnico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.service.SoporteTecnicoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/microservicios/SoporteTecnico")
public class SoporteTecnicoController {

    private SoporteTecnicoService soporteTecnicoService;

    @Autowired
    public SoporteTecnicoController(SoporteTecnicoService soporteTecnicoService) {
        this.soporteTecnicoService = soporteTecnicoService;
    }




    @Operation(summary = "Obtener todos los registros de soporte técnico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registros encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoporteTecnico.class))),
        @ApiResponse(responseCode = "204", description = "No hay registros disponibles")
    })
    @GetMapping
    public ResponseEntity<List<SoporteTecnico>> getAllInteracciones() {
        List<SoporteTecnico> lista2 = soporteTecnicoService.getAll();

        if(lista2.isEmpty()){
            //si esta vacia devuelvo un codigo not_content
            return ResponseEntity.noContent().build();
        }
        //si la lista tiene registros
        return ResponseEntity.ok(lista2);
    }



    @Operation(summary = "Crear un nuevo registro de soporte técnico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoporteTecnico.class))),
        @ApiResponse(responseCode = "400", description = "Faltan campos obligatorios"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> save(@RequestBody SoporteTecnico soporteTecnico) {
        if (soporteTecnico.getIdUsuario() == null || soporteTecnico.getDescripcion() == null || soporteTecnico.getDescripcion().isBlank()) {
            return ResponseEntity.badRequest().body("Faltan campos obligatorios");
        }

        try {
            SoporteTecnico guardado = soporteTecnicoService.save(soporteTecnico);
            return ResponseEntity.ok(guardado);
        } catch (RuntimeException ex) {
            if ("Usuario no encontrado".equals(ex.getMessage())) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }




    @Operation(summary = "Obtener un registro de soporte técnico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Soporte encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoporteTecnico.class))),
        @ApiResponse(responseCode = "404", description = "Soporte no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SoporteTecnico> obtenerSoporte(@PathVariable Long id) {
        SoporteTecnico soporte = soporteTecnicoService.obtenerSoportePorId(id);

        if (soporte == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(soporte);
    }


     @Operation(summary = "Obtener todos los soportes técnicos asociados a un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Soportes encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoporteTecnico.class)))
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<SoporteTecnico>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(soporteTecnicoService.obtenerSoportePorUsuario(idUsuario));
    }
}