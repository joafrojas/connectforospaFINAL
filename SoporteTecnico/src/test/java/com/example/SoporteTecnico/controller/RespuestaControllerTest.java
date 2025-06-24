package com.example.SoporteTecnico.controller;

import com.example.SoporteTecnico.model.Respuesta;
import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.service.RespuestaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RespuestaController.class)
public class RespuestaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RespuestaService respuestaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Respuesta respuesta;
    private SoporteTecnico soporte;

    @BeforeEach
    void setUp() {
        soporte = new SoporteTecnico();
        soporte.setIdSoporte(1L);

        respuesta = new Respuesta();
        respuesta.setIdRespuesta(10L);
        respuesta.setIdSoporteTecnico(soporte);
        respuesta.setDescripcion("Hemos revisado tu problema");
        respuesta.setFechaRespuesta(LocalDateTime.now());
    }

    @Test
    void crearRespuesta_valida_retornaCreated() throws Exception {
        when(respuestaService.crearRespuesta(any(Long.class), any(String.class)))
            .thenReturn(respuesta);

        mockMvc.perform(post("/respuestas/1")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Hemos revisado tu problema"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descripcion").value("Hemos revisado tu problema"))
            .andExpect(jsonPath("$.idRespuesta").value(10L));
    }

    @Test
    void crearRespuesta_soporteNoExiste_retornaNotFound() throws Exception {
        when(respuestaService.crearRespuesta(any(Long.class), any(String.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Soporte técnico no encontrado"));

        mockMvc.perform(post("/respuestas/99")
                .contentType(MediaType.TEXT_PLAIN)
                .content("No deberías ver esto"))
            .andExpect(status().isNotFound());
    }


    @Test
    void obtenerPorSoporte_existente_retornaLista() throws Exception {
        when(respuestaService.obtenerRespuestasPorSoporte(1L)).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/respuestas/soporte/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].descripcion").value("Hemos revisado tu problema"))
            .andExpect(jsonPath("$[0].idSoporteTecnico.idSoporte").value(1L));
    }

    @Test
    void obtenerPorSoporte_sinRespuestas_retornaNoContent() throws Exception {
        when(respuestaService.obtenerRespuestasPorSoporte(42L)).thenReturn(List.of());

        mockMvc.perform(get("/respuestas/soporte/42"))
            .andExpect(status().isNoContent());
    }
}