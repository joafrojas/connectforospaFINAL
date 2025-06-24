package com.example.MetricasSistemaConnectForoSpa.controller;

import com.example.MetricasSistemaConnectForoSpa.dto.MetricaRequest;
import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.model.TipoMetrica;
import com.example.MetricasSistemaConnectForoSpa.service.MetricaSistemaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricaSistemaController.class)
public class MetricaSistemaControllerTest {

    @MockBean
    private MetricaSistemaService metricaService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllMetricas_returnsOKAndJson() throws Exception {
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");
        MetricaSistema metrica = new MetricaSistema(1L, "Carga alta", LocalDateTime.now(), tipo);
        List<MetricaSistema> metricas = Arrays.asList(metrica);

        when(metricaService.obtenerTodas()).thenReturn(metricas);

        mockMvc.perform(get("/api/v1/metricas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id_metrica").value(1L))
            .andExpect(jsonPath("$[0].descripcion").value("Carga alta"))
            .andExpect(jsonPath("$[0].tipo.nombre").value("RENDIMIENTO"));
    }

    @Test
    void getMetricasByTipo_returnsOKAndFilteredJson() throws Exception {
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");
        MetricaSistema metrica = new MetricaSistema(1L, "Carga alta", LocalDateTime.now(), tipo);
        when(metricaService.obtenerPorTipo(1L)).thenReturn(List.of(metrica));

        mockMvc.perform(get("/api/v1/metricas/tipo/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tipo.nombre").value("RENDIMIENTO"));
    }

    @Test
    void postMetricas_returnsCreatedMetrica() throws Exception {
        MetricaRequest request = new MetricaRequest("Nueva métrica", 1L);
        TipoMetrica tipo = new TipoMetrica(1L, "FALLO");
        MetricaSistema respuesta = new MetricaSistema(1L, "Nueva métrica", LocalDateTime.now(), tipo);

        when(metricaService.registrar(request)).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/metricas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descripcion").value("Nueva métrica"));
    }

    @Test
    void putActualizarMetrica_returnsUpdatedMetrica() throws Exception {
        MetricaRequest request = new MetricaRequest("Actualizada", 2L);
        TipoMetrica tipoNuevo = new TipoMetrica(2L, "LATENCIA");
        MetricaSistema respuesta = new MetricaSistema(1L, "Actualizada", LocalDateTime.now(), tipoNuevo);

        when(metricaService.actualizar(1L, request)).thenReturn(respuesta);

        mockMvc.perform(put("/api/v1/metricas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descripcion").value("Actualizada"))
            .andExpect(jsonPath("$.tipo.nombre").value("LATENCIA"));
    }

    @Test
    void patchActualizarDescripcion_returnsMetricaConNuevaDescripcion() throws Exception {
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");
        MetricaSistema metricaActualizada = new MetricaSistema(1L, "Descripción nueva", LocalDateTime.now(), tipo);

        when(metricaService.actualizarDescripcion(1L, "Descripción nueva")).thenReturn(metricaActualizada);

        mockMvc.perform(patch("/api/v1/metricas/1/descripcion")
                .param("valor", "Descripción nueva"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descripcion").value("Descripción nueva"));
    }

    @Test
    void deleteMetrica_returnsOk() throws Exception {
        doNothing().when(metricaService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/metricas/1"))
            .andExpect(status().isOk());
    }
}

