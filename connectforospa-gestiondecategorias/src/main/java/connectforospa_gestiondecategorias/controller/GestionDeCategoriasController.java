package connectforospa_gestiondecategorias.controller;

import connectforospa_gestiondecategorias.model.Categorias;
import connectforospa_gestiondecategorias.service.GestionDeCategoriasService;
import connectforospa_gestiondecategorias.webclient.UsuarioClient;
import connectforospa_gestiondecategorias.webclient.UsuarioDTO;
import connectforospa_gestiondecategorias.webclient.RolDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Slf4j

@Tag(name = "Gestión de Categorías", description = "Permite crear, editar, eliminar y listar categorías")
public class GestionDeCategoriasController {


    private final GestionDeCategoriasService service;
    private final UsuarioClient usuarioClient;

    private boolean puedeGestionar(Long idUsuario) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerUsuario(idUsuario).block();
            return usuario != null &&
                    usuario.getRoles() != null &&
                    usuario.getRoles().stream()
                            .map(RolDTO::getNombreRol)
                            .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_MODERADOR"));
        } catch (Exception e) {
            log.error("Error al verificar permisos del usuario {}: {}", idUsuario, e.getMessage());
            return false;
        }
    }

    @Operation(summary = "Crear una nueva categoría", description = "Permite a un usuario con rol ADMIN o MODERADOR crear una categoría")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para crear categorías")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, nombre de categoría requerido")
    @ApiResponse(responseCode = "409", description = "Conflicto, ya existe una categoría con ese nombre")
    @PostMapping("/crear/{idUsuario}")
    public ResponseEntity<?> crearCategoria(@RequestBody Categorias categoria,
            @PathVariable Long idUsuario) {
        if (!puedeGestionar(idUsuario)) {
            return ResponseEntity.status(403).body(" No autorizado para crear categorías.");
        }
        return service.guardar(categoria);
    }

    @Operation(summary = "Actualizar una categoría", description = "Permite a un usuario con rol ADMIN o MODERADOR editar una categoría")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para editar categorías")
    @PutMapping("/{id}/editar/{idUsuario}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id,
            @PathVariable Long idUsuario,
            @RequestBody Categorias categoria) {
        if (!puedeGestionar(idUsuario)) {
            return ResponseEntity.status(403).body(" No autorizado para editar categorías.");
        }
        return service.actualizar(id, categoria);
    }

    @Operation(summary = "Eliminar una categoría", description = "Permite a un usuario con rol ADMIN o MODERADOR eliminar una categoría")
    @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para eliminar categorías")
    @DeleteMapping("/{id}/eliminar/{idUsuario}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id,
            @PathVariable Long idUsuario) {
        if (!puedeGestionar(idUsuario)) {
            return ResponseEntity.status(403).body(" No autorizado para eliminar categorías.");
        }
        return service.eliminar(id)
                ? ResponseEntity.ok(" Categoría eliminada correctamente.")
                : ResponseEntity.status(404).body(" Categoría no encontrada.");
    }

    @Operation(summary = "Listar todas las categorías", description = "Obtiene una lista de todas las categorías disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para listar categorías")
    @GetMapping
    public ResponseEntity<List<Categorias>> listarCategorias() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(summary = "Obtener una categoría por ID", description = "Obtiene los detalles de una categoría específica por su ID")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(" Categoría no encontrada con ID: " + id));
    }

    @Operation(summary = "Actualizar el nombre de una categoría", description = "Permite a un usuario con rol ADMIN o MODERADOR cambiar el nombre de una categoría")
    @ApiResponse(responseCode = "200", description = "Nombre de categoría actualizado exitosamente")
    @ApiResponse(responseCode = "403", description = "No autorizado para actualizar el nombre de categorías")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, nombre de categoría requerido")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")

    @PatchMapping("/{id}/nombre/{idUsuario}")
    public ResponseEntity<?> actualizarNombre(@PathVariable Long id,
            @PathVariable Long idUsuario,
            @RequestBody Map<String, String> body) {
        String nuevoNombre = body.get("nuevoNombre");
        if (!puedeGestionar(idUsuario)) {
            return ResponseEntity.status(403).body(" No autorizado...");
        }
        return service.actualizarNombre(id, nuevoNombre);
    }

    
}