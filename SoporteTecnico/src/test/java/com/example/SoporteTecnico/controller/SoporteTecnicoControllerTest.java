package com.example.SoporteTecnico.controller;

import com.example.SoporteTecnico.model.MotivoSoporte;
import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.service.SoporteTecnicoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoporteTecnicoController.class)
public class SoporteTecnicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoporteTecnicoService soporteTecnicoService;

    @Autowired
    private ObjectMapper objectMapper;

    private SoporteTecnico soporte;

    @BeforeEach
    void setUp() {
        MotivoSoporte motivo = new MotivoSoporte(1L, "Problemas en el foro");
        soporte = new SoporteTecnico();
        soporte.setIdSoporte(1L);
        soporte.setIdUsuario(10L);
        soporte.setMotivo(motivo);
        soporte.setDescripcion("No puedo responder en una publicación");
        soporte.setEstado("pendiente");
        soporte.setFechaSoporte(LocalDateTime.now());
    }

    @Test
    void obtenerTodos_CuandoHaySoportes_RetornaOk() throws Exception {
        when(soporteTecnicoService.getAll()).thenReturn(List.of(soporte));

        mockMvc.perform(get("/microservicios/SoporteTecnico"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idUsuario").value(10L))
            .andExpect(jsonPath("$[0].estado").value("pendiente"))
            .andExpect(jsonPath("$[0].descripcion").value("No puedo responder en una publicación"));
    }

    @Test
    void obtenerTodos_CuandoNoHaySoportes_RetornaNoContent() throws Exception {
        when(soporteTecnicoService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/microservicios/SoporteTecnico"))
            .andExpect(status().isNoContent());
    }

    @Test
    void guardarSoporte_Valido_RetornaOkConSoporte() throws Exception {
        when(soporteTecnicoService.save(any())).thenReturn(soporte);

        mockMvc.perform(post("/microservicios/SoporteTecnico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(soporte)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idSoporte").value(1L));
    }

    @Test
    void obtenerSoportePorId_existente_retornaOk() throws Exception {
        when(soporteTecnicoService.obtenerSoportePorId(1L)).thenReturn(soporte);

        mockMvc.perform(get("/microservicios/SoporteTecnico/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idUsuario").value(10L));
    }

    @Test
    void obtenerPorUsuario_existente_retornaLista() throws Exception {
        when(soporteTecnicoService.obtenerSoportePorUsuario(10L)).thenReturn(List.of(soporte));

        mockMvc.perform(get("/microservicios/SoporteTecnico/usuario/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].motivo.nombre_motivo").value("Problemas en el foro"));
    }

    @Test
    void guardarSoporte_CuandoUsuarioNoExiste_retornaNotFound() throws Exception {
        when(soporteTecnicoService.save(any()))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/microservicios/SoporteTecnico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(soporte)))
            .andExpect(status().isNotFound()) // antes: isInternalServerError()
            .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void obtenerSoportePorId_noExistente_retornaNotFound() throws Exception {
        when(soporteTecnicoService.obtenerSoportePorId(999L)).thenReturn(null);

        mockMvc.perform(get("/microservicios/SoporteTecnico/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void guardarSoporte_conCuerpoInvalido_retornaBadRequest() throws Exception {
        String jsonInvalido = "{\"descripcion\":\"\"}";

        mockMvc.perform(post("/microservicios/SoporteTecnico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Faltan campos obligatorios"));
    }

}