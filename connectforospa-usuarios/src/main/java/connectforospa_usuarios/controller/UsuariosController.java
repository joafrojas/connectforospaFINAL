package connectforospa_usuarios.controller;

import connectforospa_usuarios.model.Usuarios;
import connectforospa_usuarios.service.UsuariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios, roles y validaciones de seguridad")
public class UsuariosController {

        private final UsuariosService service;

        // ───── Registro y gestión básica ─────

        @Operation(summary = "Registrar un nuevo usuario")
        @PostMapping("/registrar")
        public ResponseEntity<?> crearUsuario(@RequestBody Usuarios usuario,
                        @RequestHeader(value = "Authorization", required = false) String token) {
                return service.guardar(usuario, token != null ? token : "SIN_TOKEN");
        }

        @Operation(summary = "Obtener un usuario por ID")
        @GetMapping("/{id}")
        public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
                return service.obtenerPorId(id);
        }

        @Operation(summary = "Actualizar un usuario")
        @PutMapping("/{id}")
        public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuarios usuarioActualizado) {
                return service.actualizar(id, usuarioActualizado);
        }

        @Operation(summary = "Actualizar la fecha de nacimiento de un usuario")
        @PatchMapping("/{id}/fechadenacimiento")
        public ResponseEntity<?> actualizarFechaNacimiento(@PathVariable Long id,
                        @RequestParam String fechaNacimiento) {
                return service.actualizarFechaNacimiento(id, fechaNacimiento);
        }

        // ───── Administración ─────

        @Operation(summary = "Eliminar un usuario por un administrador")
        @DeleteMapping("/admin/{idAdmin}/eliminar/{idUsuario}")
        public ResponseEntity<?> eliminarUsuarioAdmin(@PathVariable Long idAdmin, @PathVariable Long idUsuario) {
                return service.eliminarUsuarioAdmin(idAdmin, idUsuario);
        }

        @Operation(summary = "Aprobar o rechazar un usuario")
        @PutMapping("/admin/{idAdmin}/aprobar/{idUsuario}")
        public ResponseEntity<?> aprobarUsuario(@PathVariable Long idAdmin, @PathVariable Long idUsuario,
                        @RequestParam String aprobado) {
                return service.aprobarUsuario(idAdmin, idUsuario, aprobado);
        }

        @Operation(summary = "Rechazar un usuario")
        @PutMapping("/admin/{idAdmin}/usuarios/{id}/rechazar")
        public ResponseEntity<?> rechazarUsuario(@PathVariable Long idAdmin, @PathVariable Long id) {
                return service.rechazarUsuario(idAdmin, id);
        }

        // ───── Listado y filtros ─────

        @Operation(summary = "Listar todos los usuarios")
        @GetMapping("/listar")
        public ResponseEntity<?> listarTodos() {
                return ResponseEntity.ok(service.listarTodos());
        }

        @Operation(summary = "Filtrar usuarios por inicial")
        @GetMapping("/listar/filtro")
        public ResponseEntity<?> filtrarPorInicial(@RequestParam String letra) {
                return ResponseEntity.ok(service.filtrarPorInicial(letra));
        }

        // ───── Roles y permisos ─────

        @Operation(summary = "Obtener roles de un usuario")
        @GetMapping("/{id}/roles")
        public ResponseEntity<?> obtenerRoles(@PathVariable Long id) {
                return ResponseEntity.ok(service.listarRolesPorUsuario(id));
        }

        @Operation(summary = "Obtener el rol principal del usuario")
        @GetMapping("/{id}/rol")
        public ResponseEntity<String> obtenerRolPrincipal(@PathVariable Long id) {
                return ResponseEntity.ok()
                                .header("Content-Type", "text/plain")
                                .body(service.obtenerRolPrincipal(id));
        }

        @Operation(summary = "Verificar si el usuario está activo")
        @GetMapping("/{id}/activo")
        public ResponseEntity<Boolean> estaActivo(@PathVariable Long id) {
                boolean activo = service.estaActivo(id);
                return ResponseEntity.ok(activo);
        }

        // ───── Login ─────

        @Operation(summary = "Iniciar sesión de usuario")
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
                String email = credenciales.get("email");
                String password = credenciales.get("password");
                return service.login(email, password);
        }
}