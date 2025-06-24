package com.example.Comentarios2.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.Comentarios2.config.SecurityConfig;
import com.example.Comentarios2.model.Comentarios;
import com.example.Comentarios2.model.EstadoComentario;
import com.example.Comentarios2.service.ComentariosService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ComentariosController.class)
public class ComentariosControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentariosService comentariosService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(roles = "USER")
    @Test
    void getAllComentarios_conContenido_retornaOk() throws Exception {
    Comentarios comentario = new Comentarios();
    comentario.setIdComentario(1L);
    comentario.setIdUsuario(100L);
    comentario.setIdPublicacion(200L);
    comentario.setContenido("Comentario ejemplo");
    comentario.setFechaPublicacion(LocalDateTime.now());
    comentario.setEstadoComentario(new EstadoComentario(1L, "ACTIVO"));

    when(comentariosService.getAll()).thenReturn(List.of(comentario));

    mockMvc.perform(get("/microservicios/comentarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].idComentario").value(1L))
        .andExpect(jsonPath("$[0].idUsuario").value(100L))
        .andExpect(jsonPath("$[0].idPublicacion").value(200L))
        .andExpect(jsonPath("$[0].contenido").value("Comentario ejemplo"))
        .andExpect(jsonPath("$[0].estadoComentario.nombreEstado").value("ACTIVO"));
}

    @WithMockUser(roles = "USER")
    @Test
    void guardarComentario_estadoInvalido_retornaNotFound() throws Exception {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setContenido("Comentario sin estado válido");
        comentario.setEstadoComentario(new EstadoComentario(null, "INVALIDO"));

        when(comentariosService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no válido"));

        mockMvc.perform(post("/microservicios/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentario)))
            .andExpect(status().isNotFound());
    }


    @WithMockUser(roles = "USER")
    @Test
    void guardarComentario_usuarioInexistente_retornaBadRequest() throws Exception {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(999L);  // No existe
        comentario.setIdPublicacion(2L);
        comentario.setContenido("Comentario con usuario inexistente");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));

        when(comentariosService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario o publicación no encontrado"));

        mockMvc.perform(post("/microservicios/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentario)))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "USER")
    @Test
    void obtenerComentariosPorPublicacion_sinResultados_retornaNoContent() throws Exception {
        when(comentariosService.obtenerComentarioPorPublicacion(200L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/microservicios/comentarios/publicacion/200"))
            .andExpect(status().isNoContent());
    }


    @WithMockUser(roles = "USER")
    @Test
    void eliminarComentario_inexistente_retornaNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Comentario no encontrado"))
            .when(comentariosService).deletePorId(999L);

        mockMvc.perform(delete("/microservicios/comentarios/999"))
            .andExpect(status().isNotFound());
    }


    @WithMockUser(roles = "USER")
    @Test
    void guardarComentario_conJsonInvalido_retornaBadRequest() throws Exception {
        String cuerpoInvalido = """
            {
                "contenido": "Comentario sin datos esenciales"
            }
            """;

        // Simulamos que el service lanza una excepción por datos faltantes
        when(comentariosService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios"));

        mockMvc.perform(post("/microservicios/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpoInvalido))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "USER")
    @Test
    void obtenerComentariosPorUsuario_conResultados_retornaOk() throws Exception {
        Comentarios comentario = new Comentarios();
        comentario.setIdComentario(1L);
        comentario.setIdUsuario(10L);
        comentario.setIdPublicacion(20L);
        comentario.setContenido("Comentario de usuario");
        comentario.setFechaPublicacion(LocalDateTime.now());
        comentario.setEstadoComentario(new EstadoComentario(1L, "ACTIVO"));

        when(comentariosService.obtenerComentarioPorUsuario(10L))
            .thenReturn(List.of(comentario));

        mockMvc.perform(get("/microservicios/comentarios/usuario/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idComentario").value(1L))
            .andExpect(jsonPath("$[0].idUsuario").value(10L));
    }


    @WithMockUser(roles = "USER")
    @Test
    void obtenerComentariosPorUsuario_sinResultados_retornaNoContent() throws Exception {
        when(comentariosService.obtenerComentarioPorUsuario(99L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/microservicios/comentarios/usuario/99"))
            .andExpect(status().isNoContent());
    }

    
    @WithMockUser(roles = "USER")
    @Test
    void guardarComentario_foroInexistente_retornaBadRequest() throws Exception {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L); // Foro inexistente
        comentario.setContenido("Comentario con foro inexistente");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));

        when(comentariosService.save(any()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario, publicación o foro no encontrado"));

        mockMvc.perform(post("/microservicios/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentario)))
            .andExpect(status().isBadRequest());
    }

}
