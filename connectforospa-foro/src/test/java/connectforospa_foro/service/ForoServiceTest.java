package connectforospa_foro.service;

import connectforospa_foro.model.Foro;
import connectforospa_foro.repository.ForoRepository;
import connectforospa_foro.webclient.UsuarioClient;
import connectforospa_foro.webclient.UsuarioDTO;
import connectforospa_foro.webclient.RolDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForoServiceTest {

    @InjectMocks
    private ForoService foroService;

    @Mock
    private ForoRepository foroRepo;

    @Mock
    private UsuarioClient usuarioClient;

    private Foro foro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        foro = Foro.builder()
                .idForo(1L)
                .nombreForo("Foro de prueba")
                .idUsuario(1L)
                .build();
    }

    @Test
    void crearForo_deberiaRetornar200SiUsuarioEsValidoYAutorizado() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setEstado("ACTIVO");
        RolDTO rol = new RolDTO();
        rol.setNombreRol("ROLE_MODERADOR");
        usuario.setRoles(List.of(rol));

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuario);
        when(foroRepo.save(any())).thenReturn(foro);

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(foro, response.getBody());
        verify(foroRepo).save(foro);
    }

    @Test
    void crearForo_deberiaRetornar404SiUsuarioNoExiste() {
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(null);

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(foroRepo, never()).save(any());
    }

    @Test
    void crearForo_deberiaRechazarSiUsuarioEstaEliminado() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setEstado("ELIMINADO");
        usuario.setRoles(List.of(rol("ROLE_USER")));

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuario);

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("eliminado"));
    }

    @Test
    void crearForo_deberiaRechazarSiUsuarioNoTieneRoles() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setEstado("ACTIVO");
        usuario.setRoles(List.of());

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuario);

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("sin roles"));
    }

    @Test
    void crearForo_deberiaRechazarSiRolNoAutorizado() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setEstado("ACTIVO");
        usuario.setRoles(List.of(rol("ROLE_INVITADO")));

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuario);

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("no autorizado"));
    }

    @Test
    void crearForo_deberiaRetornar500SiExcepcion() {
        when(usuarioClient.obtenerUsuario(1L)).thenThrow(new RuntimeException("Boom"));

        ResponseEntity<?> response = foroService.crearForo(foro, 1L);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("interno"));
    }

    @Test
    void listar_deberiaRetornarLista() {
        when(foroRepo.findAll()).thenReturn(List.of(foro));
        List<Foro> resultado = foroService.listar();
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_deberiaRetornarSiExiste() {
        when(foroRepo.findById(1L)).thenReturn(Optional.of(foro));
        Foro resultado = foroService.obtenerPorId(1L);
        assertNotNull(resultado);
    }

    @Test
    void listarPorUsuario_deberiaRetornarAsociados() {
        when(foroRepo.findByIdUsuario(1L)).thenReturn(List.of(foro));
        List<Foro> resultado = foroService.listarPorUsuario(1L);
        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_deberiaRetornarCoincidencias() {
        when(foroRepo.findByNombreForoContainingIgnoreCase("prueba")).thenReturn(List.of(foro));
        List<Foro> resultado = foroService.buscar("prueba");
        assertEquals(1, resultado.size());
    }

    @Test
    void eliminar_deberiaEliminarSiUsuarioCoincide() {
        when(foroRepo.findById(1L)).thenReturn(Optional.of(foro));

        ResponseEntity<?> response = foroService.eliminar(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Foro eliminado con éxito.", response.getBody());
        verify(foroRepo).deleteById(1L);
    }

    @Test
    void eliminar_deberiaRetornar403SiUsuarioIncorrecto() {
        foro.setIdUsuario(2L);
        when(foroRepo.findById(1L)).thenReturn(Optional.of(foro));

        ResponseEntity<?> response = foroService.eliminar(1L, 1L);

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("No autorizado"));
        verify(foroRepo, never()).deleteById(any());
    }

    @Test
    void modificar_deberiaActualizarSiUsuarioCoincide() {
        Foro input = Foro.builder().nombreForo("Modificado").build();
        when(foroRepo.findById(1L)).thenReturn(Optional.of(foro));
        when(foroRepo.save(any())).thenReturn(foro);

        ResponseEntity<?> response = foroService.modificar(1L, 1L, input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Foro actualizado correctamente.", response.getBody());
        assertEquals("Modificado", foro.getNombreForo());
        verify(foroRepo).save(foro);
    }

    @Test
    void modificar_deberiaRetornar403SiUsuarioIncorrecto() {
        foro.setIdUsuario(2L);
        Foro input = Foro.builder().nombreForo("No válido").build();
        when(foroRepo.findById(1L)).thenReturn(Optional.of(foro));

        ResponseEntity<?> response = foroService.modificar(1L, 1L, input);

        assertEquals(403, response.getStatusCodeValue());
        verify(foroRepo, never()).save(any());
    }

    @Test
    void modificar_deberiaRetornar404SiNoExiste() {
        when(foroRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = foroService.modificar(99L, 1L, foro);

        assertEquals(404, response.getStatusCodeValue());
    }

    private RolDTO rol(String nombreRol) {
        RolDTO rol = new RolDTO();
        rol.setNombreRol(nombreRol);
        return rol;
    }
}