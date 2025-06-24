package com.example.Comentarios2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.Comentarios2.config.TokenUtils;
import com.example.Comentarios2.model.Comentarios;
import com.example.Comentarios2.model.EstadoComentario;
import com.example.Comentarios2.repository.ComentariosRepository;
import com.example.Comentarios2.repository.EstadoComentarioRepository;
import com.example.Comentarios2.webClient.ForosClient;
import com.example.Comentarios2.webClient.PublicacionesClient;
import com.example.Comentarios2.webClient.UsuariosClient;
import com.example.Comentarios2.webClient.dto.Foros;
import com.example.Comentarios2.webClient.dto.Publicacion;
import com.example.Comentarios2.webClient.dto.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ComentariosServiceTest {

    @Mock
    private ComentariosRepository comentariosRepository;

    @Mock
    private EstadoComentarioRepository estadoComentarioRepository;

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private PublicacionesClient publicacionesClient;

    @Mock
    private ForosClient forosClient;

    @InjectMocks
    private ComentariosService comentariosService;

    private Comentarios comentario;
    private EstadoComentario estado;

    @BeforeEach
    void setUp() {
        estado = new EstadoComentario(null, "ACTIVO");

        comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Buen aporte!");
        comentario.setFechaPublicacion(LocalDateTime.now());
        comentario.setEstadoComentario(estado);
    }

    @Test
    void guardarComentario_CuandoTodoValido_RetornaGuardado() {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Este es un comentario válido");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));
        comentario.setFechaPublicacion(LocalDateTime.now());

        when(usuariosClient.obtenerUsuarioPorId(1L, "mock-token"))
            .thenReturn(Mono.just(new Usuario()));
        when(publicacionesClient.obtenerPublicacionPorId(2L))
            .thenReturn(Mono.just(new Publicacion()));
        when(forosClient.obtenerForoPorId(3L))
            .thenReturn(Mono.just(new Foros()));
        when(estadoComentarioRepository.findByNombreEstado("ACTIVO"))
            .thenReturn(Optional.of(new EstadoComentario(1L, "ACTIVO")));
        when(comentariosRepository.save(any(Comentarios.class)))
            .thenAnswer(i -> i.getArgument(0));

        try (MockedStatic<TokenUtils> tokenMock = mockStatic(TokenUtils.class)) {
            tokenMock.when(TokenUtils::extraerTokenDelContexto).thenReturn("mock-token");

            Comentarios resultado = comentariosService.save(comentario);

            assertNotNull(resultado);
            assertEquals("ACTIVO", resultado.getEstadoComentario().getNombreEstado());
            verify(comentariosRepository).save(any(Comentarios.class));
        }
    }


    @Test
    void guardarComentario_EstadoInvalido_LanzaExcepcion() {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Comentario de prueba");
        comentario.setFechaPublicacion(LocalDateTime.now());
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));

        try (MockedStatic<TokenUtils> tokenMock = mockStatic(TokenUtils.class)) {
            tokenMock.when(TokenUtils::extraerTokenDelContexto).thenReturn("mock-token");

            when(usuariosClient.obtenerUsuarioPorId(1L, "mock-token")).thenReturn(Mono.just(new Usuario()));
            when(publicacionesClient.obtenerPublicacionPorId(2L)).thenReturn(Mono.just(new Publicacion()));
            when(forosClient.obtenerForoPorId(3L)).thenReturn(Mono.just(new Foros()));
            when(estadoComentarioRepository.findByNombreEstado("ACTIVO")).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                comentariosService.save(comentario);
            });

            assertEquals("404 NOT_FOUND \"Estado no válido: ACTIVO\"", ex.getMessage());
        }
    }


    @Test
    void guardarComentario_UsuarioNoExiste_LanzaExcepcion() {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Intento con usuario inexistente");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));
        comentario.setFechaPublicacion(LocalDateTime.now());

        try (MockedStatic<TokenUtils> tokenMock = mockStatic(TokenUtils.class)) {
            tokenMock.when(TokenUtils::extraerTokenDelContexto).thenReturn("mock-token");

            when(usuariosClient.obtenerUsuarioPorId(1L, "mock-token")).thenReturn(Mono.empty());
            when(publicacionesClient.obtenerPublicacionPorId(2L)).thenReturn(Mono.just(new Publicacion()));
            when(forosClient.obtenerForoPorId(3L)).thenReturn(Mono.just(new Foros()));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                comentariosService.save(comentario);
            });

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Usuario, publicación o foro no encontrado"));
        }
    }


    @Test
    void guardarComentario_PublicacionNoExiste_LanzaExcepcion() {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Comentario sin publicación válida");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));
        comentario.setFechaPublicacion(LocalDateTime.now());

        try (MockedStatic<TokenUtils> tokenMock = mockStatic(TokenUtils.class)) {
            tokenMock.when(TokenUtils::extraerTokenDelContexto).thenReturn("mock-token");

            when(usuariosClient.obtenerUsuarioPorId(1L, "mock-token")).thenReturn(Mono.just(new Usuario()));
            when(publicacionesClient.obtenerPublicacionPorId(2L)).thenReturn(Mono.empty());
            when(forosClient.obtenerForoPorId(3L)).thenReturn(Mono.just(new Foros()));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                comentariosService.save(comentario);
            });

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Usuario, publicación o foro no encontrado"));
        }
    }


    @Test
    void guardarComentario_ForoNoExiste_LanzaExcepcion() {
        Comentarios comentario = new Comentarios();
        comentario.setIdUsuario(1L);
        comentario.setIdPublicacion(2L);
        comentario.setIdForo(3L);
        comentario.setContenido("Comentario con foro inexistente");
        comentario.setEstadoComentario(new EstadoComentario(null, "ACTIVO"));
        comentario.setFechaPublicacion(LocalDateTime.now());

        try (MockedStatic<TokenUtils> tokenMock = mockStatic(TokenUtils.class)) {
            tokenMock.when(TokenUtils::extraerTokenDelContexto).thenReturn("mock-token");

            when(usuariosClient.obtenerUsuarioPorId(1L, "mock-token")).thenReturn(Mono.just(new Usuario()));
            when(publicacionesClient.obtenerPublicacionPorId(2L)).thenReturn(Mono.just(new Publicacion()));
            when(forosClient.obtenerForoPorId(3L)).thenReturn(Mono.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                comentariosService.save(comentario);
            });

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Usuario, publicación o foro no encontrado"));
        }
    }

}
