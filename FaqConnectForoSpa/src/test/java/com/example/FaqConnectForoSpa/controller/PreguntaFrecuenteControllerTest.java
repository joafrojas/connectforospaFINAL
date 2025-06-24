package com.example.FaqConnectForoSpa.controller;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import com.example.FaqConnectForoSpa.service.PreguntaFrecuenteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreguntaFrecuenteController.class)
public class PreguntaFrecuenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreguntaFrecuenteService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_debeRetornarLista() throws Exception {
        List<PreguntaFrecuente> preguntas = Arrays.asList(
            new PreguntaFrecuente(1L, "¿Cómo resetear contraseña?", "Sigue estos pasos...")
        );

        when(service.getAll()).thenReturn(preguntas);

        mockMvc.perform(get("/api/v1/faqs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].pregunta").value("¿Cómo resetear contraseña?"));
    }

    @Test
    void getById_debeRetornarPregunta() throws Exception {
        PreguntaFrecuente pregunta = new PreguntaFrecuente(1L, "¿Cómo crear cuenta?", "Simplemente...");

        when(service.getById(1L)).thenReturn(pregunta);

        mockMvc.perform(get("/api/v1/faqs/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pregunta").value("¿Cómo crear cuenta?"));
    }

    @Test
    void postCrear_debeCrearPregunta() throws Exception {
        PreguntaFrecuente nueva = new PreguntaFrecuente(null, "¿Cómo cambiar email?", "Aquí...");
        PreguntaFrecuente guardada = new PreguntaFrecuente(1L, "¿Cómo cambiar email?", "Aquí...");

        when(service.save(nueva)).thenReturn(guardada);

        mockMvc.perform(post("/api/v1/faqs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void putActualizar_debeActualizarPregunta() throws Exception {
        PreguntaFrecuente cambios = new PreguntaFrecuente(null, "Pregunta nueva", "Respuesta nueva");
        PreguntaFrecuente actualizada = new PreguntaFrecuente(1L, "Pregunta nueva", "Respuesta nueva");

        when(service.update(1L, cambios)).thenReturn(actualizada);

        mockMvc.perform(put("/api/v1/faqs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cambios)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pregunta").value("Pregunta nueva"));
    }

    @Test
    void deleteEliminar_debeEliminarPregunta() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/faqs/1"))
            .andExpect(status().isOk());
    }

    @Test
    void buscarPorPalabra_debeRetornarListaFiltrada() throws Exception {
        String palabra = "contraseña";
        PreguntaFrecuente p1 = new PreguntaFrecuente(1L, "¿Cómo resetear contraseña?", "Instrucciones...");
        PreguntaFrecuente p2 = new PreguntaFrecuente(2L, "¿Qué hacer si olvido contraseña?", "Pasos...");

        when(service.filtrarPorPalabraClave(palabra)).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/v1/faqs/buscar")
                .param("palabra", palabra))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].pregunta").value("¿Cómo resetear contraseña?"))
            .andExpect(jsonPath("$[1].pregunta").value("¿Qué hacer si olvido contraseña?"));
    }
}
