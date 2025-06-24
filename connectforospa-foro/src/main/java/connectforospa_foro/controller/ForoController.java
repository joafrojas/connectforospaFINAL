package connectforospa_foro.controller;

import connectforospa_foro.model.Foro;
import connectforospa_foro.service.ForoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/foros")
@RequiredArgsConstructor
@Tag(name = "Foros", description = "Gestión de foros, incluyendo creación, modificación, eliminación y búsqueda")
public class ForoController {

    private final ForoService service;

    @Operation(summary = "Crear un nuevo foro", description = "Permite a un usuario crear un foro")
    @PostMapping("/crear/{idUsuario}")
    public ResponseEntity<?> crear(@PathVariable Long idUsuario, @RequestBody Foro foro) {
        return service.crearForo(foro, idUsuario);
    }

    @Operation(summary = "Listar todos los foros", description = "Devuelve una lista de todos los foros")
    @GetMapping
    public List<Foro> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener foro por ID", description = "Devuelve un foro específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Foro> obtener(@PathVariable Long id) {
        Foro foro = service.obtenerPorId(id);
        return foro != null ? ResponseEntity.ok(foro) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Listar foros por usuario", description = "Devuelve una lista de foros creados por un usuario específico")
    @GetMapping("/usuario/{idUsuario}")
    public List<Foro> porUsuario(@PathVariable Long idUsuario) {
        return service.listarPorUsuario(idUsuario);
    }

    @Operation(summary = "Buscar foros por nombre", description = "Devuelve una lista de foros que coinciden con el nombre dado")
    @GetMapping("/buscar/{nombre}")
    public List<Foro> buscar(@PathVariable String nombre) {
        return service.buscar(nombre);
    }

    @Operation(summary = "Eliminar foro (solo autor o admin)", description = "Permite a un usuario eliminar un foro específico, solo si es el autor o tiene rol de administrador")
    @DeleteMapping("/eliminar/{idForo}/{idUsuario}")
    public ResponseEntity<?> eliminar(@PathVariable Long idForo, @PathVariable Long idUsuario) {
        return service.eliminar(idForo, idUsuario);
    }

    @Operation(summary = "Modificar foro (solo autor)", description = "Permite a un usuario modificar un foro específico")
    @PatchMapping("/modificar/{idForo}/{idUsuario}")
    public ResponseEntity<?> modificar(@PathVariable Long idForo, @PathVariable Long idUsuario,
                                       @RequestBody Foro foro) {
        return service.modificar(idForo, idUsuario, foro);
    }
}