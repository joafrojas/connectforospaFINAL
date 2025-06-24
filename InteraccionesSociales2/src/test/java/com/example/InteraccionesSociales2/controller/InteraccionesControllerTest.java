package com.example.InteraccionesSociales2.controller;

import com.example.InteraccionesSociales2.model.Interacciones;
import com.example.InteraccionesSociales2.model.TipoInteraccion;
import com.example.InteraccionesSociales2.service.InteraccionesService;
import com.example.InteraccionesSociales2.webClient.PublicacionesClient;
import com.example.InteraccionesSociales2.webClient.UsuariosClient;

import reactor.core.publisher.Mono;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(InteraccionesController.class)
public class InteraccionesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InteraccionesService interaccionesService;

    @MockBean
    private UsuariosClient usuariosClient;

    @MockBean
    private PublicacionesClient publicacionesClient;

    @Autowired
    private ObjectMapper objectMapper; 




    @Test
    void getAllInteracciones_returnsOkAndJson() throws Exception {
        // Crear datos simulados
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");
        
        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setFechaPublicacion(LocalDateTime.now());
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);
        interaccion.setIdForo(5L); // Si tu modelo lo tiene

        when(interaccionesService.getAll()).thenReturn(List.of(interaccion));

        mockMvc.perform(get("/interacciones"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idInteraccion").value(1L))
            .andExpect(jsonPath("$[0].idUsuario").value(10L))
            .andExpect(jsonPath("$[0].idPublicacion").value(20L))
            .andExpect(jsonPath("$[0].tipoInteraccion.nombre").value("like"));
    }


    @Test
    void guardarInteraccion_conUsuarioOPublicacionInexistente_retornaBadRequest() throws Exception {
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(999L);
        interaccion.setIdPublicacion(888L);

        when(interaccionesService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "no se ha encontrado la publicación o el usuario"));

        mockMvc.perform(post("/interacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interaccion)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("no se ha encontrado la publicación o el usuario"));
    }


    @Test
    void guardarInteraccion_valida_retornaCreated() throws Exception {
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);

        when(interaccionesService.save(any()))
            .thenReturn(interaccion);

        mockMvc.perform(post("/interacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interaccion)))
            .andExpect(status().isCreated());
    }

    @Test
    void eliminarInteraccion_existente_retornaNoContent() throws Exception {
        doNothing().when(interaccionesService).deletePorId(1L);

        mockMvc.perform(delete("/interacciones/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void obtenerInteraccionesPorPublicacion_existente_retornaOk() throws Exception {
        // Datos simulados
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);

        List<Interacciones> resultadoSimulado = Collections.singletonList(interaccion);

        when(interaccionesService.obtenerInteraccionPorPublicacion(20L)).thenReturn(resultadoSimulado);

        mockMvc.perform(get("/interacciones/publicacion/20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idInteraccion").value(1L))
            .andExpect(jsonPath("$[0].idUsuario").value(10L))
            .andExpect(jsonPath("$[0].idPublicacion").value(20L))
            .andExpect(jsonPath("$[0].tipoInteraccion.nombre").value("like"));
    }

    @Test
    void obtenerInteraccionesPorPublicacion_sinResultados_retornaNoContent() throws Exception {
        Long idPublicacion = 99L;

        when(interaccionesService.obtenerInteraccionPorPublicacion(idPublicacion))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/interacciones/publicacion/" + idPublicacion))
            .andExpect(status().isNoContent());
    }

    @Test
    void obtenerInteraccionesPorUsuario_existente_retornaOk() throws Exception {
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);

        List<Interacciones> resultadoSimulado = Collections.singletonList(interaccion);

        when(interaccionesService.obtenerInteraccionesPorUsuario(10L))
            .thenReturn(resultadoSimulado);

        mockMvc.perform(get("/interacciones/usuario/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idInteraccion").value(1L))
            .andExpect(jsonPath("$[0].idUsuario").value(10L))
            .andExpect(jsonPath("$[0].idPublicacion").value(20L))
            .andExpect(jsonPath("$[0].tipoInteraccion.nombre").value("like"));
    }

    @Test
    void obtenerInteraccionesPorUsuario_sinResultados_retornaNoContent() throws Exception {
        when(interaccionesService.obtenerInteraccionesPorUsuario(77L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/interacciones/usuario/77"))
            .andExpect(status().isNoContent());
    }

    @Test
    void obtenerInteraccionPorId_existente_retornaOk() throws Exception {
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(100L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);

        when(interaccionesService.obtenerInteraccionPorId(100L))
            .thenReturn(interaccion);

        mockMvc.perform(get("/interacciones/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idInteraccion").value(100L))
            .andExpect(jsonPath("$.idUsuario").value(10L))
            .andExpect(jsonPath("$.idPublicacion").value(20L))
            .andExpect(jsonPath("$.tipoInteraccion.nombre").value("like"));
    }

    @Test
    void obtenerInteraccionPorId_inexistente_retornaNoContent() throws Exception {
        when(interaccionesService.obtenerInteraccionPorId(999L))
            .thenReturn(null);

        mockMvc.perform(get("/interacciones/999"))
            .andExpect(status().isNoContent());
    }

    @Test
    void eliminarInteraccion_inexistente_retornaNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la interacción con ID 999"))
            .when(interaccionesService).deletePorId(999L);

        mockMvc.perform(delete("/interacciones/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("No se encontró la interacción con ID 999"));
    }



    @Test
    void guardarInteraccion_conTipoInteraccionInvalido_retornaNotFound() throws Exception {
        TipoInteraccion tipoInvalido = new TipoInteraccion(99L, "megadislike");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipoInvalido);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);

        when(interaccionesService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de interacción no válido"));

        mockMvc.perform(post("/interacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interaccion)))
            .andExpect(status().isNotFound());
    }


    @Test
    void guardarInteraccion_conForoInexistente_retornaBadRequest() throws Exception {
        TipoInteraccion tipo = new TipoInteraccion(1L, "like");

        Interacciones interaccion = new Interacciones();
        interaccion.setIdInteraccion(1L);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(20L);
        interaccion.setIdForo(30L);

        when(interaccionesService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foro no encontrado"));

        mockMvc.perform(post("/interacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interaccion)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Foro no encontrado"));
    }


}
