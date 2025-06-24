package com.example.ReportesyModeracionConnectForoSpa.controller;

import com.example.ReportesyModeracionConnectForoSpa.model.EstadoReporte;
import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;
import com.example.ReportesyModeracionConnectForoSpa.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
public class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Autowired
    private ObjectMapper objectMapper;

    EstadoReporte pendiente = new EstadoReporte(1L, "PENDIENTE");

    @Test
    void postCrearReporte_returnsOK() throws Exception {
        Reporte request = new Reporte(null, "Contenido ofensivo", null, null, 1001L, 2001L, pendiente);
        Reporte respuesta = new Reporte(1L, "Contenido ofensivo", null, LocalDateTime.now(), 1001L, 2001L, pendiente);

        when(reporteService.crearReporte(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/reportes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.motivo").value("Contenido ofensivo"));
    }

    @Test
    void getObtenerTodos_returnsList() throws Exception {
        Reporte r = new Reporte(1L, "Spam", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteService.obtenerTodos()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/reportes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].motivo").value("Spam"));
    }

    @Test
    void getObtenerPorId_returnsReporte() throws Exception {
        Reporte r = new Reporte(1L, "Spam", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteService.obtenerPorId(1L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/reportes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPorGenerador_returnsFiltrado() throws Exception {
        Reporte r = new Reporte(1L, "Insulto", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteService.obtenerPorGenerador(1001L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/reportes/generador/1001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idUsuarioGenerador").value(1001));
    }

    @Test
    void getPorReportado_returnsFiltrado() throws Exception {
        Reporte r = new Reporte(1L, "Insulto", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteService.obtenerPorReportado(2001L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/reportes/reportado/2001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idUsuarioReportado").value(2001));
    }

    @Test
    void putActualizarReporte_returnsActualizado() throws Exception {
        Reporte actualizado = new Reporte(1L, "Nuevo motivo", "Revisado", LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteService.actualizarReporte(eq(1L), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/reportes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.motivo").value("Nuevo motivo"));
    }

    @Test
    void patchActualizarEstado_returnsOK() throws Exception {
        Reporte actualizado = new Reporte(1L, "Spam", null, LocalDateTime.now(), 1001L, 2001L, new EstadoReporte(2L, "RESUELTO"));

        when(reporteService.actualizarEstado(1L, 2L)).thenReturn(actualizado);

        mockMvc.perform(patch("/api/v1/reportes/1/estado")
                .param("nuevoEstadoId", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estadoReporte.nombre").value("RESUELTO"));
    }

    @Test
    void deleteReporte_returnsOk() throws Exception {
        doNothing().when(reporteService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/reportes/1"))
            .andExpect(status().isOk());

        verify(reporteService, times(1)).eliminar(1L);
    }

    @Test
    void deleteReporte_notFound() throws Exception {
        // Simula que el servicio lanza 404 cuando no existe el reporte
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No se puede eliminar: reporte no encontrado."))
            .when(reporteService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/reportes/99"))
            .andExpect(status().isNotFound());

        verify(reporteService, times(1)).eliminar(99L);
    }
}
