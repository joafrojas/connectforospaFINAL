package connectforospa_gestiondecategorias.service;

import connectforospa_gestiondecategorias.model.Categorias;
import connectforospa_gestiondecategorias.repository.GestionDeCategoriasRepository;
import connectforospa_gestiondecategorias.webclient.RolDTO;
import connectforospa_gestiondecategorias.webclient.UsuarioClient;
import connectforospa_gestiondecategorias.webclient.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GestionDeCategoriasServiceTest {

    @Mock
    private GestionDeCategoriasRepository repo;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private GestionDeCategoriasService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void puedeGestionarCategorias_DeberiaRetornarTrue_ParaAdmin() {
        
        RolDTO rolAdmin = new RolDTO();
        rolAdmin.setNombreRol("ROLE_ADMIN");

        UsuarioDTO admin = new UsuarioDTO();
        admin.setRoles(List.of(rolAdmin));

        
        when(usuarioClient.obtenerUsuario(anyLong())).thenReturn(Mono.just(admin));

        
        boolean resultado = service.puedeGestionarCategorias(1L);

        
        assertTrue(resultado, "Se esperaba que el usuario con ROLE_ADMIN pudiera gestionar categorías");
    }


    @Test
    void puedeGestionarCategorias_DeberiaRetornarFalse_SiNoTieneRolValido() {
        RolDTO rolUser = new RolDTO();
        rolUser.setNombreRol("ROLE_USER");
        UsuarioDTO user = new UsuarioDTO();
        user.setRoles(List.of(rolUser));
        when(usuarioClient.obtenerUsuario(2L)).thenReturn(Mono.just(user));

        boolean resultado = service.puedeGestionarCategorias(2L);

        assertFalse(resultado);
    }

    @Test
    void guardar_DeberiaCrearCategoriaNueva() {
        Categorias nueva = new Categorias();
        nueva.setNombre("DevOps");
        nueva.setDescripcion("Automatización");

        when(repo.findByNombreIgnoreCase("DevOps")).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> {
            Categorias c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        var response = service.guardar(nueva);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void guardar_DeberiaRechazarSiNombreDuplicado() {
        Categorias duplicada = new Categorias();
        duplicada.setNombre("Backend");
        duplicada.setDescripcion("Ya existe");
        when(repo.findByNombreIgnoreCase("Backend")).thenReturn(Optional.of(new Categorias()));

        var response = service.guardar(duplicada);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void eliminar_DeberiaEliminarSiExiste() {
        when(repo.existsById(5L)).thenReturn(true);

        boolean resultado = service.eliminar(5L);

        assertTrue(resultado);
        verify(repo).deleteById(5L);
    }

    @Test
    void actualizarNombre_DeberiaActualizarCorrectamente() {
        Categorias existente = new Categorias();
        existente.setId(3L);
        existente.setNombre("Antiguo");
        existente.setDescripcion("Texto");
        when(repo.findById(3L)).thenReturn(Optional.of(existente));

        var response = service.actualizarNombre(3L, "NuevoNombre");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(" Nombre de la categoría actualizado: NuevoNombre", response.getBody());
    }
}