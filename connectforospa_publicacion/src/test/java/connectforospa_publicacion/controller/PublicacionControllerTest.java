package connectforospa_publicacion.controller;

import connectforospa_publicacion.model.EstadoPublicacion;
import connectforospa_publicacion.model.Publicacion;
import connectforospa_publicacion.service.PublicacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@WebFluxTest(PublicacionController.class)
class PublicacionControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private PublicacionService publicacionService;

    private Publicacion publicacion;

    @BeforeEach
    void setup() {
        publicacion = new Publicacion();
        publicacion.setId(1L);
        publicacion.setTitulo("Test título");
        publicacion.setContenido("Contenido de prueba");
        publicacion.setIdCategoria(1L);
        publicacion.setIdForo(2L);
        publicacion.setIdUsuario(3L);
        publicacion.setFechaPublicacion(LocalDateTime.now());
        publicacion.setEstadoPublicacion(EstadoPublicacion.PENDIENTE);
    }

    @Test
    void crearPublicacion_DeberiaRetornarOk() {
        Mockito.when(publicacionService.guardarPublicacion(any())).thenReturn(Mono.just(publicacion));

        webClient.post()
                .uri("/api/v1/publicaciones/crear/3")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "titulo": "Test título",
                      "contenido": "Contenido de prueba",
                      "idForo": 2,
                      "idCategoria": 1
                    }
                """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(res -> res.contains("Publicación enviada"));
    }

    @Test
    void moderarPublicacion_DeberiaRetornarOk() {
        publicacion.setEstadoPublicacion(EstadoPublicacion.APROBADA);
        Mockito.when(publicacionService.moderarPublicacion(eq(1L), eq(10L), eq("APROBAR")))
                .thenReturn(publicacion);

        webClient.put()
                .uri("/api/v1/publicaciones/1/moderar/10?accion=APROBAR")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> body.contains("APROBADA"));
    }

    @Test
    void eliminarPorModerador_UsuarioAutorizado_DeberiaEliminar() {
        Mockito.when(publicacionService.eliminarComoModerador(eq(1L), eq(2L))).thenReturn(true);

        webClient.delete()
                .uri("/api/v1/publicaciones/1/moderar/2/eliminar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> body.contains("eliminada"));
    }
    @Test
    void eliminarPorModerador_UsuarioNoAutorizado_DeberiaRetornarForbidden() {
        Mockito.when(publicacionService.eliminarComoModerador(eq(1L), eq(2L))).thenReturn(false);

        webClient.delete()
                .uri("/api/v1/publicaciones/1/moderar/2/eliminar")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class)
                .value(body -> body.contains("No autorizado"));
    }
}