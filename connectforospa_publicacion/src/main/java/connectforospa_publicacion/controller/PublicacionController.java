package connectforospa_publicacion.controller;

import connectforospa_publicacion.model.Publicacion;
import connectforospa_publicacion.service.PublicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publicaciones")
@RequiredArgsConstructor
@Tag(name = "Publicaciones", description = "Gestión de publicaciones, incluyendo creación, moderación y consulta")
public class PublicacionController {

    private final PublicacionService service;

    @Operation(summary = "Crear una nueva publicación", description = "Permite a un usuario crear una publicación en un foro")
    @ApiResponse(responseCode = "200", description = "Publicación creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de publicación requeridos")
    @ApiResponse(responseCode = "403", description = "No autorizado para crear publicaciones")

    @PostMapping("/crear/{idUsuario}")
    public Mono<ResponseEntity<String>> crearPublicacion(@PathVariable Long idUsuario,
            @RequestBody Publicacion publicacion) {
        publicacion.setIdUsuario(idUsuario); 
        return service.guardarPublicacion(publicacion)
                .map(p -> ResponseEntity.ok(" Publicación creada con estado: " + p.getEstadoPublicacion()))
                .defaultIfEmpty(ResponseEntity.badRequest().body(" No se pudo crear la publicación."));
    }

    @Operation(summary = "Modificar una publicación", description = "Permite a un moderador modificar el estado de una publicación")
    @ApiResponse(responseCode = "200", description = "Publicación modificada exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para modificar esta publicación")
    @ApiResponse(responseCode = "404", description = "Publicación no encontrada")

    @PutMapping("/{id}/moderar/{idModerador}")
    public ResponseEntity<?> moderar(@PathVariable Long id,
            @PathVariable Long idModerador,
            @RequestParam String accion) {
        var resultado = service.moderarPublicacion(id, idModerador, accion);
        return (resultado != null)
                ? ResponseEntity.ok(" Publicación actualizada a estado: " + resultado.getEstadoPublicacion())
                : ResponseEntity.status(403).body(" Sin permisos para moderar o publicación no encontrada.");
    }

    @Operation(summary = "Eliminar una publicación", description = "Permite a un moderador eliminar una publicación")
    @ApiResponse(responseCode = "200", description = "Publicación eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar esta publicación")
    @ApiResponse(responseCode = "404", description = "Publicación no encontrada")

    @DeleteMapping("/{id}/moderar/{idModerador}/eliminar")
    public ResponseEntity<?> eliminarPorModerador(@PathVariable Long id,
            @PathVariable Long idModerador) {
        return service.eliminarComoModerador(id, idModerador)
                ? ResponseEntity.ok("🗑️ Publicación eliminada.")
                : ResponseEntity.status(403).body(" No autorizado para eliminar esta publicación.");
    }

    @Operation(summary = "Listar todas las publicaciones", description = "Devuelve una lista de todas las publicaciones")
    @ApiResponse(responseCode = "200", description = "Lista de publicaciones obtenida exitosamente")
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Publicacion>> publicacionesPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(service.listarPorCategoria(idCategoria));
    }

    @Operation(summary = "Listar publicaciones por usuario", description = "Devuelve una lista de publicaciones creadas por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de publicaciones del usuario obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Publicacion>> publicacionesDeUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.obtenerPublicacionesPorUsuario(idUsuario));
    }

    @Operation(summary = "Listar publicaciones aprobadas", description = "Devuelve una lista de todas las publicaciones aprobadas")
    @ApiResponse(responseCode = "200", description = "Lista de publicaciones aprobadas obtenida exitosamente")
    @GetMapping("/aprobadas")
    public ResponseEntity<List<Publicacion>> aprobadas() {
        return ResponseEntity.ok(service.listarPublicacionesAprobadas());
    }

    @Operation(summary = "Obtener detalles de una publicación", description = "Devuelve los detalles de una publicación específica por su ID")
    @ApiResponse(responseCode = "200", description = "Publicación encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        var pub = service.obtenerPorId(id);
        return (pub != null) ? ResponseEntity.ok(pub)
                : ResponseEntity.status(404).body(" Publicación no encontrada.");
    }

    @Operation(summary = "Actualizar una publicación", description = "Permite actualizar los detalles de una publicación")  
    @ApiResponse(responseCode = "200", description = "Publicación actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Publicación no encontrada")
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Publicacion publicacion) {
        var pubActualizada = service.actualizarPublicacion(id, publicacion);
        return (pubActualizada != null)
                ? ResponseEntity.ok(" Publicación actualizada con estado: " + pubActualizada.getEstadoPublicacion())
                : ResponseEntity.status(404).body(" Publicación no encontrada.");
    }
}