package com.example.FaqConnectForoSpa.controller;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import com.example.FaqConnectForoSpa.service.PreguntaFrecuenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Preguntas Frecuentes", description = "API para gestionar preguntas frecuentes")
@RestController
@RequestMapping("/api/v1/faqs")
@RequiredArgsConstructor
public class PreguntaFrecuenteController {

    private final PreguntaFrecuenteService service;

    @Operation(summary = "Obtener todas las preguntas frecuentes")
    @GetMapping
    public List<PreguntaFrecuente> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Obtener pregunta frecuente por ID")
    @GetMapping("/{id}")
    public PreguntaFrecuente getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Crear una nueva pregunta frecuente")
    @PostMapping
    public PreguntaFrecuente crear(@RequestBody PreguntaFrecuente pregunta) {
        return service.save(pregunta);
    }

    @Operation(summary = "Actualizar una pregunta frecuente existente")
    @PutMapping("/{id}")
    public PreguntaFrecuente actualizar(@PathVariable Long id, @RequestBody PreguntaFrecuente pregunta) {
        return service.update(id, pregunta);
    }

    @Operation(summary = "Eliminar una pregunta frecuente por ID")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Buscar preguntas frecuentes por palabra clave")
    @GetMapping("/buscar")
    public List<PreguntaFrecuente> buscarPorPalabra(@RequestParam String palabra) {
        return service.filtrarPorPalabraClave(palabra);
    }
}