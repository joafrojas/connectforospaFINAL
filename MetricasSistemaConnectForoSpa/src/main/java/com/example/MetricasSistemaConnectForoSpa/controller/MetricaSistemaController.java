package com.example.MetricasSistemaConnectForoSpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MetricasSistemaConnectForoSpa.dto.MetricaRequest;
import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.service.MetricaSistemaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Métricas del Sistema", description = "API para gestionar métricas del sistema")
@RestController
@RequestMapping("/api/v1/metricas")
public class MetricaSistemaController {

    @Autowired
    private MetricaSistemaService service;

    @Operation(summary = "Registrar una nueva métrica")
    @PostMapping
    public MetricaSistema registrar(@RequestBody MetricaRequest request) {
        return service.registrar(request);
    }

    @Operation(summary = "Obtener todas las métricas")
    @GetMapping
    public List<MetricaSistema> obtenerTodas() {
        return service.obtenerTodas();
    }

    @Operation(summary = "Obtener métricas por tipo")
    @GetMapping("/tipo/{id}")
    public List<MetricaSistema> obtenerPorTipo(@PathVariable Long id) {
        return service.obtenerPorTipo(id);
    }

    @Operation(summary = "Actualizar una métrica por ID")
    @PutMapping("/{id}")
    public MetricaSistema actualizar(@PathVariable Long id, @RequestBody MetricaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Actualizar solo la descripción de una métrica")
    @PatchMapping("/{id}/descripcion")
    public MetricaSistema actualizarDescripcion(@PathVariable Long id, @RequestParam String valor) {
        return service.actualizarDescripcion(id, valor);
    }

    @Operation(summary = "Eliminar una métrica por ID")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}