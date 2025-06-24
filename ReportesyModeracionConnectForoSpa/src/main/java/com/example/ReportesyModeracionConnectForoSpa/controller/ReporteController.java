package com.example.ReportesyModeracionConnectForoSpa.controller;

import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;
import com.example.ReportesyModeracionConnectForoSpa.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes y Moderación", description = "API para gestionar reportes y moderación")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Crear un nuevo reporte", description = "Crea un reporte basado en la información enviada")
    @ApiResponse(responseCode = "200", description = "Reporte creado exitosamente")
    @PostMapping
    public Reporte crear(@RequestBody Reporte reporte) {
        return reporteService.crearReporte(reporte);
    }

    @Operation(summary = "Obtener todos los reportes", description = "Lista todos los reportes existentes")
    @GetMapping
    public List<Reporte> obtenerTodos() {
        return reporteService.obtenerTodos();
    }

    @Operation(summary = "Obtener reporte por ID", description = "Obtiene un reporte específico por su ID")
    @GetMapping("/{id}")
    public Reporte obtenerPorId(
        @Parameter(description = "ID del reporte a buscar", required = true)
        @PathVariable Long id) {
        return reporteService.obtenerPorId(id);
    }

    @Operation(summary = "Filtrar reportes por usuario generador", description = "Obtiene todos los reportes generados por un usuario específico")
    @GetMapping("/generador/{id}")
    public List<Reporte> porGenerador(
        @Parameter(description = "ID del usuario generador", required = true)
        @PathVariable Long id) {
        return reporteService.obtenerPorGenerador(id);
    }

    @Operation(summary = "Filtrar reportes por usuario reportado", description = "Obtiene todos los reportes dirigidos a un usuario específico")
    @GetMapping("/reportado/{id}")
    public List<Reporte> porReportado(
        @Parameter(description = "ID del usuario reportado", required = true)
        @PathVariable Long id) {
        return reporteService.obtenerPorReportado(id);
    }

    @Operation(summary = "Actualizar un reporte completo", description = "Actualiza toda la información de un reporte existente")
    @PutMapping("/{id}")
    public Reporte actualizar(
        @Parameter(description = "ID del reporte a actualizar", required = true)
        @PathVariable Long id,
        @RequestBody Reporte nuevoReporte) {
        return reporteService.actualizarReporte(id, nuevoReporte);
    }

    @Operation(summary = "Actualizar solo el estado de un reporte", description = "Modifica únicamente el estado de un reporte")
    @PatchMapping("/{id}/estado")
    public Reporte actualizarEstado(
        @Parameter(description = "ID del reporte", required = true)
        @PathVariable Long id,
        @Parameter(description = "ID del nuevo estado", required = true)
        @RequestParam Long nuevoEstadoId) {
        return reporteService.actualizarEstado(id, nuevoEstadoId);
    }

    @Operation(summary = "Eliminar un reporte", description = "Elimina un reporte por su ID")
    @DeleteMapping("/{id}")
    public void eliminar(
        @Parameter(description = "ID del reporte a eliminar", required = true)
        @PathVariable Long id) {
        reporteService.eliminar(id);
    }
}
