package connectforospa_publicacion.service;

import connectforospa_publicacion.model.EstadoPublicacion;
import connectforospa_publicacion.model.Publicacion;
import connectforospa_publicacion.repository.PublicacionRepository;
import connectforospa_publicacion.webclient.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionServiceTest {

    @Mock
    private PublicacionRepository publicacionRepo;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private CategoriaClient categoriaClient;
    @Mock
    private ForoClient foroClient;

    @InjectMocks
    private PublicacionService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarPublicacion_DeberiaGuardarAprobada_SiUsuarioEsAdmin() {
        Long idUsuario = 1L;
        Publicacion publicacion = new Publicacion();
        publicacion.setIdUsuario(idUsuario);
        publicacion.setIdCategoria(3L);
        publicacion.setIdForo(2L);

        UsuarioDTO admin = new UsuarioDTO();
        admin.setEstado("ACTIVO");
        RolDTO rolAdmin = new RolDTO();
        rolAdmin.setNombreRol("ROLE_ADMIN");
        admin.setRoles(List.of(rolAdmin));

        when(usuarioClient.obtenerUsuario(idUsuario)).thenReturn(Mono.just(admin));
        when(categoriaClient.validarCategoria(3L)).thenReturn(Mono.just(true));
        when(foroClient.validarForo(2L)).thenReturn(Mono.just(true));
        when(publicacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Publicacion result = service.guardarPublicacion(publicacion).block();

        assertNotNull(result);
        assertEquals(EstadoPublicacion.APROBADA, result.getEstadoPublicacion());
        assertNotNull(result.getFechaPublicacion());
    }

    @Test
    void guardarPublicacion_DeberiaGuardarPendiente_SiUsuarioEsUser() {
        Publicacion publicacion = new Publicacion();
        publicacion.setIdUsuario(2L);
        publicacion.setIdCategoria(1L);
        publicacion.setIdForo(1L);

        UsuarioDTO user = new UsuarioDTO();
        user.setEstado("ACTIVO");
        RolDTO rol = new RolDTO();
        rol.setNombreRol("ROLE_USER");
        user.setRoles(List.of(rol));

        when(usuarioClient.obtenerUsuario(2L)).thenReturn(Mono.just(user));
        when(categoriaClient.validarCategoria(1L)).thenReturn(Mono.just(true));
        when(foroClient.validarForo(1L)).thenReturn(Mono.just(true));
        when(publicacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Publicacion result = service.guardarPublicacion(publicacion).block();

        assertNotNull(result);
        assertEquals(EstadoPublicacion.PENDIENTE, result.getEstadoPublicacion());
    }

    @Test
    void guardarPublicacion_DeberiaRetornarVacio_SiUsuarioEsEliminado() {
        Publicacion publicacion = new Publicacion();
        publicacion.setIdUsuario(99L);

        UsuarioDTO eliminado = new UsuarioDTO();
        eliminado.setEstado("ELIMINADO");

        when(usuarioClient.obtenerUsuario(99L)).thenReturn(Mono.just(eliminado));

        assertTrue(service.guardarPublicacion(publicacion).blockOptional().isEmpty());
    }

    @Test
    void guardarPublicacion_DeberiaRetornarVacio_SiCategoriaNoValida() {
        Publicacion pub = new Publicacion();
        pub.setIdUsuario(3L);
        pub.setIdCategoria(100L);
        pub.setIdForo(1L);

        UsuarioDTO user = new UsuarioDTO();
        user.setEstado("ACTIVO");
        RolDTO rolUser = new RolDTO();
        rolUser.setNombreRol("ROLE_USER");
        user.setRoles(List.of(rolUser));

        when(usuarioClient.obtenerUsuario(3L)).thenReturn(Mono.just(user));
        when(categoriaClient.validarCategoria(100L)).thenReturn(Mono.just(false));

        assertTrue(service.guardarPublicacion(pub).blockOptional().isEmpty());
    }

    @Test
    void guardarPublicacion_DeberiaRetornarVacio_SiFalloValidacionForo() {
        Publicacion pub = new Publicacion();
        pub.setIdUsuario(3L);
        pub.setIdCategoria(1L);
        pub.setIdForo(99L);

        UsuarioDTO user = new UsuarioDTO();
        user.setEstado("ACTIVO");
        RolDTO rolUser = new RolDTO();
        rolUser.setNombreRol("ROLE_USER");
        user.setRoles(List.of(rolUser));

        when(usuarioClient.obtenerUsuario(3L)).thenReturn(Mono.just(user));
        when(categoriaClient.validarCategoria(1L)).thenReturn(Mono.just(true));
        when(foroClient.validarForo(99L)).thenReturn(Mono.just(false));

        assertTrue(service.guardarPublicacion(pub).blockOptional().isEmpty());
    }
    @Test
    void moderarPublicacion_DeberiaRetornarAprobada_SiModeradorAdmin() {
        Publicacion publicacion = new Publicacion();
        publicacion.setId(1L);
        publicacion.setEstadoPublicacion(EstadoPublicacion.PENDIENTE);

        UsuarioDTO moderador = new UsuarioDTO();
        moderador.setEstado("ACTIVO");
        RolDTO rolModerador = new RolDTO();
        rolModerador.setNombreRol("ROLE_MODERADOR");
        moderador.setRoles(List.of(rolModerador));

        when(usuarioClient.obtenerUsuario(10L)).thenReturn(Mono.just(moderador));
        when(publicacionRepo.findById(1L)).thenReturn(java.util.Optional.of(publicacion));
        when(publicacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Publicacion result = service.moderarPublicacion(1L, 10L, "APROBAR");

        assertNotNull(result);
        assertEquals(EstadoPublicacion.APROBADA, result.getEstadoPublicacion());
    }
}