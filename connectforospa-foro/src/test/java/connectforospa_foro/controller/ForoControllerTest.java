package connectforospa_foro.controller;

import connectforospa_foro.model.Foro;
import connectforospa_foro.service.ForoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(ForoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForoService service;

    @Test
    void deberiaCrearForo() throws Exception {
        when(service.crearForo(any(Foro.class), eq(1L)))
            .thenReturn(ResponseEntity.status(201).body("Foro creado exitosamente."));

        mockMvc.perform(post("/api/v1/foros/crear/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombreForo": "Debate Inteligente",
                          "idUsuario": 1
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("creado exitosamente")));
    }

    @Test
    void deberiaListarForos() throws Exception {
        when(service.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/foros"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deberiaObtenerForoPorId() throws Exception {
        Foro foro = Foro.builder().idForo(5L).nombreForo("TechTalks").idUsuario(1L).build();
        when(service.obtenerPorId(5L)).thenReturn(foro);

        mockMvc.perform(get("/api/v1/foros/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreForo").value("TechTalks"));
    }

    @Test
    void deberiaListarForosPorUsuario() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/foros/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deberiaBuscarForoPorNombre() throws Exception {
        when(service.buscar("gaming")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/foros/buscar/gaming"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deberiaEliminarForoSiExiste() throws Exception {
        when(service.eliminar(10L, 1L))
            .thenReturn(ResponseEntity.ok("Foro eliminado con éxito."));

        mockMvc.perform(delete("/api/v1/foros/eliminar/10/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Foro eliminado con éxito."));
    }

    @Test
    void deberiaRetornarNotFoundAlEliminarForoInexistente() throws Exception {
        when(service.eliminar(99L, 1L))
            .thenReturn(ResponseEntity.status(404).body("Foro no encontrado."));

        mockMvc.perform(delete("/api/v1/foros/eliminar/99/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Foro no encontrado."));
    }

    @Test
    void deberiaModificarForoSiExiste() throws Exception {
        when(service.modificar(eq(1L), eq(2L), any(Foro.class)))
            .thenReturn((ResponseEntity<String>) ResponseEntity.ok("Foro actualizado correctamente."));

        mockMvc.perform(patch("/api/v1/foros/modificar/1/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombreForo": "Modificado"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Foro actualizado correctamente."));
    }

    @Test
    void deberiaRetornarNotFoundSiNoExisteAlModificar() throws Exception {
        when(service.modificar(eq(33L), eq(1L), any(Foro.class)))
            .thenReturn(ResponseEntity.status(404).body("Foro no encontrado."));

        mockMvc.perform(patch("/api/v1/foros/modificar/33/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombreForo": "FakeForum"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Foro no encontrado."));
    }

    @Test
    void deberiaRetornar403SiUsuarioNoEsAutor() throws Exception {
        when(service.modificar(eq(1L), eq(9L), any(Foro.class)))
            .thenReturn(ResponseEntity.status(403).body("No autorizado para modificar este foro."));

        mockMvc.perform(patch("/api/v1/foros/modificar/1/9")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombreForo": "Hackeado"
                        }
                        """))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No autorizado para modificar este foro."));
    }
}