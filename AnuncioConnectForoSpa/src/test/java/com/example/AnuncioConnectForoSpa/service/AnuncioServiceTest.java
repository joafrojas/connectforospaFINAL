package com.example.AnuncioConnectForoSpa.service;

import com.example.AnuncioConnectForoSpa.dto.AnuncioRequest;
import com.example.AnuncioConnectForoSpa.model.Anuncio;
import com.example.AnuncioConnectForoSpa.model.Estado;
import com.example.AnuncioConnectForoSpa.model.Rol;
import com.example.AnuncioConnectForoSpa.model.Usuario;
import com.example.AnuncioConnectForoSpa.repository.AnuncioRepository;
import com.example.AnuncioConnectForoSpa.repository.EstadoRepository;
import com.example.AnuncioConnectForoSpa.repository.RolRepository;
import com.example.AnuncioConnectForoSpa.webclient.UsuarioClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono; // <<< Asegúrate de tener este import

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnuncioServiceTest {

    @InjectMocks
    private AnuncioService anuncioService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioClient usuarioClient; // <<<<<< AGREGA ESTE MOCK

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearConUsuarioValidoRoleAnuncios_guardadoCorrectamente() {
        // Arrange
        AnuncioRequest request = new AnuncioRequest();
        request.setIdUsuario(1L);
        request.setIdEstado(1L);
        request.setTitulo("Test");
        request.setFoto("foto.jpg");
        request.setPrioridad("Alta");

        Estado estado = new Estado(1L, "ACTIVO");
        Usuario usuario = new Usuario(1L);
        Rol rol = Rol.builder().nombreRol("ROLE_ANUNCIOS").usuario(usuario).build();

        when(usuarioClient.tieneRolAnuncios(anyLong())).thenReturn(Mono.just(true)); // <<<< COMPORTAMIENTO DEL MOCK
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Anuncio resultado = anuncioService.create(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("Test");
        verify(anuncioRepository, times(1)).save(any(Anuncio.class));
    }

    @Test
    void crearConUsuarioSinPermiso_lanzaForbidden() {
        AnuncioRequest request = new AnuncioRequest();
        request.setIdUsuario(5L);
        request.setIdEstado(1L);
        request.setTitulo("SinPermiso");
        request.setFoto("foto2.jpg");
        request.setPrioridad("Baja");

        when(usuarioClient.tieneRolAnuncios(anyLong())).thenReturn(Mono.just(false)); // <<<< USUARIO SIN PERMISO

        Throwable thrown = catchThrowable(() -> anuncioService.create(request));

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    @Test
    void crearConEstadoInexistente_lanzaNotFound() {
        AnuncioRequest request = new AnuncioRequest();
        request.setIdUsuario(1L);
        request.setIdEstado(999L); // Estado inexistente
        request.setTitulo("TestEstadoInexistente");
        request.setFoto("foto3.jpg");
        request.setPrioridad("Media");

        Usuario usuario = new Usuario(1L);
        Rol rol = Rol.builder().nombreRol("ROLE_ANUNCIOS").usuario(usuario).build();

        when(usuarioClient.tieneRolAnuncios(anyLong())).thenReturn(Mono.just(true)); // Usuario con permiso
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(estadoRepository.findById(999L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> anuncioService.create(request));

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND");
    }
}
