package connectforospa_gestiondecategorias.controller;

import connectforospa_gestiondecategorias.model.Categorias;
import connectforospa_gestiondecategorias.service.GestionDeCategoriasService;
import connectforospa_gestiondecategorias.webclient.RolDTO;
import connectforospa_gestiondecategorias.webclient.UsuarioClient;
import connectforospa_gestiondecategorias.webclient.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestionDeCategoriasController.class)
class GestionDeCategoriasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GestionDeCategoriasService service;

    @MockBean
    private UsuarioClient usuarioClient;

    private UsuarioDTO admin;

    @BeforeEach
    void setup() {
        admin = new UsuarioDTO();
        RolDTO adminRole = new RolDTO();
        adminRole.setNombreRol("ROLE_ADMIN");
        admin.setRoles(List.of(adminRole));

        Mockito.when(usuarioClient.obtenerUsuario(eq(1L)))
                .thenReturn(reactor.core.publisher.Mono.just(admin));
    }

    @Test
    void crearCategoria_Autorizado_DeberiaRetornar201() throws Exception {
        ResponseEntity<?> respuesta = ResponseEntity.status(201).body("Categoría creada: Backend");

        Mockito.when(service.guardar(any(Categorias.class))).thenReturn((ResponseEntity) respuesta);

        mockMvc.perform(post("/api/v1/categorias/crear/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "nombre": "Backend",
                              "descripcion": "Servicios y lógica de negocio"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Categoría creada: Backend")));
    }

    @Test
    void eliminarCategoria_NoAutorizado_DeberiaRetornar403() throws Exception {
        UsuarioDTO user = new UsuarioDTO();
        RolDTO userRole = new RolDTO();
        userRole.setNombreRol("ROLE_USER");
        user.setRoles(List.of(userRole));

        Mockito.when(usuarioClient.obtenerUsuario(2L))
                .thenReturn(reactor.core.publisher.Mono.just(user));

        mockMvc.perform(delete("/api/v1/categorias/10/eliminar/2"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("No autorizado")));
    }

    @Test
    void listarCategorias_DeberiaRetornar200ConLista() throws Exception {
        List<Categorias> mockList = List.of(
                new Categorias(1L, "Backend", "Servicios"),
                new Categorias(2L, "Frontend", "Interfaces"));

        Mockito.when(service.listarTodas()).thenReturn(mockList);

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Backend"));
    }

    @Test
    void obtenerCategoria_Existente_DeberiaRetornar200() throws Exception {
        Categorias cat = new Categorias(7L, "Seguridad", "Autenticación y permisos");
        Mockito.when(service.obtenerPorId(7L)).thenReturn(Optional.of(cat));

        mockMvc.perform(get("/api/v1/categorias/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Seguridad"));
    }

    @Test
    void actualizarNombre_Autorizado_DeberiaRetornar200() throws Exception {

        Mockito.when(service.actualizarNombre(5L, "Infraestructura"))
                .thenReturn(ResponseEntity.ok("✏️ Nombre de la categoría actualizado: Infraestructura"));

        mockMvc.perform(patch("/api/v1/categorias/5/nombre/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nuevoNombre\": \"Infraestructura\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Infraestructura")));
    }
}