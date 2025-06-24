package com.example.Notificaciones2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Notificaciones2.model.Notificaciones;
import com.example.Notificaciones2.repository.NotificacionesRepository;
import com.example.Notificaciones2.webClient.UsuarioClient;
import com.example.Notificaciones2.webClient.dto.Usuario;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class NotificacionesServiceTest {

    @Mock
    private NotificacionesRepository notificacionesRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private NotificacionesService notificacionesService;

    private Notificaciones notificacion;


     @BeforeEach
    void setUp() {
        notificacion = new Notificaciones();
        notificacion.setIdUsuario(1L);
        notificacion.setMensaje("Mensaje de prueba");
        notificacion.setEstado("ACTIVO");
        notificacion.setFechaEnvio(LocalDateTime.now());
    }

    @Test
    void save_CuandoUsuarioExiste_RetornaNotificacionGuardada() {
        Usuario usuario = new Usuario();
        usuario.setUsuario(1L);

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(Mono.just(usuario));
        when(notificacionesRepository.save(any(Notificaciones.class))).thenAnswer(i -> i.getArgument(0));

        Notificaciones resultado = notificacionesService.save(notificacion);

        assertNotNull(resultado);
        assertEquals("Mensaje de prueba", resultado.getMensaje());
        verify(notificacionesRepository).save(any(Notificaciones.class));
    }

    @Test
    void save_CuandoMensajeEsNull_LanzaExcepcion() {
        Notificaciones notificacion = new Notificaciones();
        notificacion.setIdUsuario(1L);
        notificacion.setMensaje(null);

        assertThrows(IllegalArgumentException.class, () -> {
            notificacionesService.save(notificacion);
        });
    }



}
