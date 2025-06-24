package connectforospa_usuarios.service;

import connectforospa_usuarios.model.Rol;
import connectforospa_usuarios.model.Usuarios;
import connectforospa_usuarios.repository.RegistroUsuarioRepository;
import connectforospa_usuarios.repository.UsuarioRolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UsuariosServiceTest {

    @InjectMocks
    private UsuariosService service;

    @Mock
    private RegistroUsuarioRepository usuarioRepo;

    @Mock
    private UsuarioRolRepository rolRepo;

    @Mock
    private PasswordEncoder encoder;

    private Usuarios nuevo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        nuevo = Usuarios.builder()
                .nombreUsuario("joan")
                .email("joan@example.com")
                .password("1234")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void deberiaRegistrarUsuarioConRolUser() {
        when(usuarioRepo.findByEmail(anyString())).thenReturn(null);
        when(usuarioRepo.existsByNombreUsuario(anyString())).thenReturn(false);
        when(usuarioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(encoder.encode(anyString())).thenReturn("hash");

        ResponseEntity<?> res = service.guardar(nuevo, "Bearer tokenX");

        assertEquals(201, res.getStatusCodeValue());
        assertTrue(res.getBody().toString().contains("ROLE_USER"));
    }

    @Test
    void deberiaActualizarUsuario() {
        Usuarios existente = Usuarios.builder().idUsuario(1L).build();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(existente));
        when(encoder.encode(anyString())).thenReturn("hashed");

        Usuarios input = Usuarios.builder().nombre("Nuevo").email("nuevo@x.com")
                .password("123").fechaNacimiento(LocalDate.of(1990, 1, 1)).build();

        ResponseEntity<?> res = service.actualizar(1L, input);

        assertEquals(200, res.getStatusCodeValue());
        verify(usuarioRepo).save(argThat(u -> u.getNombre().equals("Nuevo")));
    }

    @Test
    void deberiaActualizarFechaNacimiento() {
        Usuarios u = new Usuarios();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(u));

        ResponseEntity<?> res = service.actualizarFechaNacimiento(1L, "2001-12-12");

        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void deberiaRechazarUsuarioConRolPendiente() {
        Rol r = new Rol(); r.setNombreRol("PENDIENTE_ADMIN");
        Usuarios u = new Usuarios(); u.setRoles(List.of(r));

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(adminValido()));
        when(usuarioRepo.findById(2L)).thenReturn(Optional.of(u));

        ResponseEntity<?> res = service.rechazarUsuario(1L, 2L);
        assertEquals(200, res.getStatusCodeValue());
        assertTrue(res.getBody().toString().contains("rechazado"));
    }

    @Test
    void deberiaEliminarUsuarioSiEsAdmin() {
        Usuarios u = Usuarios.builder().estado("ACTIVO").build();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(adminValido()));
        when(usuarioRepo.findById(2L)).thenReturn(Optional.of(u));

        ResponseEntity<?> res = service.eliminarUsuarioAdmin(1L, 2L);
        assertEquals(200, res.getStatusCodeValue());
        assertEquals("ELIMINADO", u.getEstado());
    }

    @Test
    void deberiaFiltrarUsuariosPorInicial() {
        Usuarios u1 = Usuarios.builder().nombreUsuario("joan").build();
        Usuarios u2 = Usuarios.builder().nombreUsuario("mario").build();
        when(usuarioRepo.findAll()).thenReturn(List.of(u1, u2));

        List<Usuarios> resultado = service.filtrarPorInicial("j");
        assertEquals(1, resultado.size());
        assertEquals("joan", resultado.get(0).getNombreUsuario());
    }

    @Test
    void deberiaRetornarUsuarioPorId() {
        Usuarios u = Usuarios.builder().idUsuario(1L).build();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(u));

        ResponseEntity<Usuarios> res = service.obtenerPorId(1L);
        assertEquals(200, res.getStatusCodeValue());
        assertEquals(1L, res.getBody().getIdUsuario());
    }

    @Test
    void deberiaListarRolesPorUsuario() {
        Rol r1 = new Rol(); r1.setNombreRol("ROLE_USER");
        Rol r2 = new Rol(); r2.setNombreRol("ROLE_MODERADOR");

        Usuarios u = new Usuarios(); u.setRoles(List.of(r1, r2));
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(u));

        List<Rol> roles = service.listarRolesPorUsuario(1L);
        assertEquals(2, roles.size());
        assertEquals("ROLE_USER", roles.get(0).getNombreRol());
    }

    private Usuarios adminValido() {
        Rol admin = new Rol(); admin.setNombreRol("ROLE_ADMIN");
        Usuarios u = new Usuarios(); u.setRoles(List.of(admin));
        return u;
    }
    @Test
    void login_exitoso_devuelveUsuarioConRoles() {
        
        Usuarios usuarioMock = new Usuarios();
        usuarioMock.setIdUsuario(1L);
        usuarioMock.setNombreUsuario("admin");
        usuarioMock.setEmail("admin@connectforo.cl");
        usuarioMock.setPassword("encodedpass");
        usuarioMock.setRoles(List.of(new Rol(null, "ROLE_ADMIN", "admin", usuarioMock)));

        when(usuarioRepo.findByEmail("admin@connectforo.cl")).thenReturn(usuarioMock);
        when(encoder.matches("123456", "encodedpass")).thenReturn(true);

        
        ResponseEntity<?> response = service.login("admin@connectforo.cl", "123456");

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("admin", body.get("nombreUsuario"));
        assertTrue(((List<?>) body.get("roles")).contains("ROLE_ADMIN"));
    }

    @Test
    void login_credencialesInvalidas_devuelveUnauthorized() {
        
        when(usuarioRepo.findByEmail("fake@correo.cl")).thenReturn(null);

        ResponseEntity<?> response = service.login("fake@correo.cl", "123456");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas.", response.getBody());
    }

    @Test
    void login_passwordIncorrecta_devuelveUnauthorized() {
        Usuarios usuario = new Usuarios();
        usuario.setEmail("admin@connectforo.cl");
        usuario.setPassword("encodedpass");

        when(usuarioRepo.findByEmail("admin@connectforo.cl")).thenReturn(usuario);
        when(encoder.matches("wrongpass", "encodedpass")).thenReturn(false);

        ResponseEntity<?> response = service.login("admin@connectforo.cl", "wrongpass");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas.", response.getBody());
    }
}

