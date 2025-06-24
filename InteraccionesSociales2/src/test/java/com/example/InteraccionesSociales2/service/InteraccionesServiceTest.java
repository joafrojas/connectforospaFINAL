package com.example.InteraccionesSociales2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.example.InteraccionesSociales2.model.Interacciones;
import com.example.InteraccionesSociales2.model.TipoInteraccion;
import com.example.InteraccionesSociales2.repository.InteraccionesRepository;
import com.example.InteraccionesSociales2.repository.TipoInteraccionRepository;
import com.example.InteraccionesSociales2.webClient.ForosClient;
import com.example.InteraccionesSociales2.webClient.PublicacionesClient;
import com.example.InteraccionesSociales2.webClient.UsuariosClient;
import com.example.InteraccionesSociales2.webClient.dto.Foros;
import com.example.InteraccionesSociales2.webClient.dto.Publicacion;
import com.example.InteraccionesSociales2.webClient.dto.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class InteraccionesServiceTest {

    @Mock
    private InteraccionesRepository interaccionesRepository;

    @Mock
    private PublicacionesClient publicacionesClient;

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private ForosClient forosClient;

    @Mock
    private TipoInteraccionRepository tipoInteraccionRepository;

    @InjectMocks
    private InteraccionesService interaccionesService;

    private Interacciones interaccion;
    private TipoInteraccion tipoMock;

    @BeforeEach
    void setUp() {
        tipoMock = new TipoInteraccion(1L, "like");

        interaccion = new Interacciones();
        interaccion.setIdUsuario(10L);
        interaccion.setIdPublicacion(99L);
        interaccion.setIdForo(5L);
        interaccion.setTipoInteraccion(tipoMock);
    }

    @Test
    void guardarInteraccion_CuandoTodoValido_RetornaInteraccionGuardada() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(10L);

        Publicacion publicacion = new Publicacion();
        publicacion.setIdPublicacion(99L);

        Foros foro = new Foros();
        foro.setIdForo(5L);

        when(usuariosClient.obtenerUsuarioPorId(10L, "SIN_TOKEN")).thenReturn(Mono.just(usuario));
        when(publicacionesClient.obtenerPorId(99L)).thenReturn(Mono.just(publicacion));
        when(forosClient.obtenerPorId(5L)).thenReturn(Mono.just(foro));
        when(tipoInteraccionRepository.findByNombre("like")).thenReturn(Optional.of(tipoMock));
        when(interaccionesRepository.save(any(Interacciones.class))).thenAnswer(i -> i.getArgument(0));

        Interacciones resultado = interaccionesService.save(interaccion);

        assertNotNull(resultado);
        assertEquals("like", resultado.getTipoInteraccion().getNombre());
        verify(interaccionesRepository).save(any(Interacciones.class));
    }
}