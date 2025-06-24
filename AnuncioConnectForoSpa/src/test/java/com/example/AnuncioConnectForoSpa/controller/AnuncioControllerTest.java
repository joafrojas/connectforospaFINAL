package com.example.AnuncioConnectForoSpa.controller;

import com.example.AnuncioConnectForoSpa.dto.AnuncioRequest;
import com.example.AnuncioConnectForoSpa.dto.AnuncioResponse;
import com.example.AnuncioConnectForoSpa.model.Anuncio;
import com.example.AnuncioConnectForoSpa.service.AnuncioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnuncioController.class)
public class AnuncioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnuncioService anuncioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsList() throws Exception {
        List<AnuncioResponse> anuncios = Arrays.asList(
                new AnuncioResponse(1L, "Título 1", LocalDateTime.now(), "ALTA", "ACTIVO", "Usuario 1"));

        when(anuncioService.getAll()).thenReturn(anuncios);

        mockMvc.perform(get("/api/v1/anuncios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Título 1"));
    }

    @Test
    void getRecientes_returnsFiltered() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        when(anuncioService.getByFecha(fecha)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/anuncios/recientes")
                .param("desde", fecha.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPorPrioridad_returnsFiltered() throws Exception {
        when(anuncioService.getPorPrioridad("ALTA")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/anuncios/prioridad")
                .param("valor", "ALTA"))
                .andExpect(status().isOk());
    }

    @Test
    void postCrearAnuncio_returnsCreated() throws Exception {
        AnuncioRequest request = new AnuncioRequest();
        Anuncio simulado = new Anuncio();
        when(anuncioService.create(any(AnuncioRequest.class))).thenReturn(simulado);

        mockMvc.perform(post("/api/v1/anuncios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void putActualizarAnuncio_returnsUpdated() throws Exception {
        AnuncioRequest request = new AnuncioRequest();
        Anuncio simulado = new Anuncio();
        when(anuncioService.update(eq(1L), any(AnuncioRequest.class))).thenReturn(simulado);

        mockMvc.perform(put("/api/v1/anuncios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void patchActualizarPrioridad_returnsOk() throws Exception {
        Anuncio simulado = new Anuncio();
        when(anuncioService.updatePrioridad(1L, "ALTA")).thenReturn(simulado);

        mockMvc.perform(patch("/api/v1/anuncios/1/prioridad")
                .param("nuevaPrioridad", "ALTA"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAnuncio_returnsNoContent() throws Exception {
        doNothing().when(anuncioService).delete(1L);

        mockMvc.perform(delete("/api/v1/anuncios/1"))
                .andExpect(status().isNoContent());

        verify(anuncioService, times(1)).delete(1L);
    }
}
