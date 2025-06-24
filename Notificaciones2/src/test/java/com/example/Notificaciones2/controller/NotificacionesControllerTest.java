package com.example.Notificaciones2.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.Notificaciones2.model.Notificaciones;
import com.example.Notificaciones2.service.NotificacionesService;
import com.example.Notificaciones2.webClient.UsuarioClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(NotificacionesController.class)
public class NotificacionesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionesService notificacionesService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioClient usuarioClient;



    @Test
    void getAll_cuandoHayNotificaciones_retornaOk() throws Exception {
       Notificaciones noti = new Notificaciones(1L, 2L, "Texto", "ACTIVO", LocalDateTime.now());
        when(notificacionesService.obtenerNotificacionPorId(1L)).thenReturn(noti);

        when(notificacionesService.getAll()).thenReturn(List.of(noti));

        mockMvc.perform(get("/microservicios/notificaciones"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idNotificacion").value(1L))
            .andExpect(jsonPath("$[0].mensaje").value("Texto"))
            .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    void getAll_cuandoListaVacia_retornaNoContent() throws Exception {
        when(notificacionesService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/microservicios/notificaciones"))
            .andExpect(status().isNoContent());
    }


    @Test
    void obtenerPorId_cuandoExiste_retornaOk() throws Exception {
        Notificaciones noti = new Notificaciones(1L, 2L, "Texto", "ACTIVO", LocalDateTime.now());

        when(notificacionesService.obtenerNotificacionPorId(1L)).thenReturn(noti);

        mockMvc.perform(get("/microservicios/notificaciones/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idNotificacion").value(1L));
    }

     @Test
    void obtenerPorId_cuandoNoExiste_retornaNotFound() throws Exception {
        when(notificacionesService.obtenerNotificacionPorId(999L)).thenReturn(null);

        mockMvc.perform(get("/microservicios/notificaciones/999"))
            .andExpect(status().isNotFound());
    }


    @Test
    void obtenerPorUsuario_conResultados_retornaOk() throws Exception {
        Notificaciones noti = new Notificaciones(1L, 7L, "Texto", "ACTIVO", LocalDateTime.now());

        when(notificacionesService.obtenerNotificacionPorUsuario(7L))
            .thenReturn(List.of(noti));

        mockMvc.perform(get("/microservicios/notificaciones/usuario/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idUsuario").value(7L));
    }

    @Test
    void obtenerPorUsuario_sinResultados_retornaNoContent() throws Exception {
        when(notificacionesService.obtenerNotificacionPorUsuario(8L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/microservicios/notificaciones/usuario/8"))
            .andExpect(status().isNoContent());
    }

    @Test
    void guardar_valido_retornaOk() throws Exception {
        Notificaciones entrada = new Notificaciones(null, 5L, "Nuevo", "ACTIVO", null);
        Notificaciones salida = new Notificaciones(1L, 5L, "Nuevo", "ACTIVO", LocalDateTime.now());

        when(notificacionesService.save(any())).thenReturn(salida);

        mockMvc.perform(post("/microservicios/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entrada)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idNotificacion").value(1L))
            .andExpect(jsonPath("$.mensaje").value("Nuevo"));
    }

    @Test
    void guardar_usuarioNoExiste_retornaInternalServerError() throws Exception {
        Notificaciones entrada = new Notificaciones(null, 99L, "Error", "ACTIVO", null);

        when(notificacionesService.save(any()))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/microservicios/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entrada)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void eliminar_existente_retornaNoContent() throws Exception {
        doNothing().when(notificacionesService).delete(3L);

        mockMvc.perform(delete("/microservicios/notificaciones/3"))
            .andExpect(status().isNoContent());
    }

}
