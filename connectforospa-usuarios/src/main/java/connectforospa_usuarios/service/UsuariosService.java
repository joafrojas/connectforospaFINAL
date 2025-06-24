package connectforospa_usuarios.service;

import connectforospa_usuarios.model.Rol;
import connectforospa_usuarios.model.Usuarios;
import connectforospa_usuarios.repository.RegistroUsuarioRepository;
import connectforospa_usuarios.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final RegistroUsuarioRepository usuarioRepo;
    private final UsuarioRolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<?> guardar(Usuarios usuario, String token) {
        if (usuario.getEmail() == null || usuario.getPassword() == null ||
                usuario.getNombreUsuario() == null || usuario.getFechaNacimiento() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
        }

        Usuarios existente = usuarioRepo.findByEmail(usuario.getEmail());
        if (existente != null) {
            if ("ELIMINADO".equals(existente.getEstado())) {
                existente.setEstado("ACTIVO");
                usuarioRepo.save(existente);
                return ResponseEntity.ok("Usuario reactivado con ID: " + existente.getIdUsuario());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con este correo.");
        }

        if (usuarioRepo.existsByNombreUsuario(usuario.getNombreUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un nombre de usuario registrado.");
        }

        usuario.setFechaCreacion(LocalDateTime.now());
        String rolNombre = asignarRol(usuario.getEmail(), token);
        usuario.setEstado(rolNombre.startsWith("PENDIENTE_") ? "PENDIENTE" : "ACTIVO");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuarios usuarioGuardado = usuarioRepo.save(usuario);

        Rol rol = new Rol();
        rol.setNombreRol(rolNombre);
        rol.setNombreUsuario(usuarioGuardado.getNombreUsuario());
        rol.setUsuario(usuarioGuardado);
        rolRepo.save(rol);

        String mensaje = rolNombre.startsWith("PENDIENTE_")
                ? "Usuario registrado con rol pendiente (" + rolNombre
                        + "). Espere aprobación por parte del administrador."
                : "Usuario registrado exitosamente con rol: " + rolNombre;

        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    private String asignarRol(String email, String token) {
        String clean = (token != null) ? token.replace("Bearer", "").trim().toUpperCase() : "";

        if (email.endsWith("@connectforo.cl"))
            return "TOKEN_ADMIN".equals(clean) ? "ROLE_ADMIN" : "PENDIENTE_ADMIN";
        if (email.endsWith("@moderador.connectforo.cl"))
            return "TOKEN_MODERADOR".equals(clean) ? "ROLE_MODERADOR" : "PENDIENTE_MODERADOR";
        if (email.endsWith("@anuncios.connectforo.cl"))
            return "TOKEN_ANUNCIOS".equals(clean) ? "ROLE_ANUNCIOS" : "PENDIENTE_ANUNCIOS";
        if (email.endsWith("@soporte.connectforo.cl"))
            return "TOKEN_SOPORTE".equals(clean) ? "ROLE_SOPORTE" : "PENDIENTE_SOPORTE";

        return "ROLE_USER";
    }

    private boolean esAdminValido(Long idAdmin) {
        return usuarioRepo.findById(idAdmin)
                .map(user -> user.getRoles().stream()
                        .anyMatch(r -> "ROLE_ADMIN".equals(r.getNombreRol())))
                .orElse(false);
    }

    public ResponseEntity<?> aprobarUsuario(Long idAdmin, Long idUsuario, String aprobado) {
        if (!esAdminValido(idAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un administrador puede aprobar usuarios.");
        }

        return usuarioRepo.findById(idUsuario).map(usuario -> {
            String pendienteEsperado = "PENDIENTE_" + aprobado.replace("ROLE_", "");
            Optional<Rol> pendiente = usuario.getRoles().stream()
                    .filter(r -> r.getNombreRol().equals(pendienteEsperado))
                    .findFirst();

            if (pendiente.isEmpty()) {
                return ResponseEntity.badRequest().body("El usuario no tiene el rol pendiente: " + pendienteEsperado);
            }

            Rol rol = pendiente.get();
            rol.setNombreRol(aprobado);
            rolRepo.save(rol);

            usuario.setEstado("ACTIVO");
            usuarioRepo.save(usuario);

            return ResponseEntity.ok("Usuario aprobado como: " + aprobado);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }

    public ResponseEntity<?> rechazarUsuario(Long idAdmin, Long idUsuario) {
        if (!esAdminValido(idAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un administrador puede rechazar usuarios.");
        }

        return usuarioRepo.findById(idUsuario).map(usuario -> {
            Optional<Rol> pendiente = usuario.getRoles().stream()
                    .filter(r -> r.getNombreRol().startsWith("PENDIENTE_"))
                    .findFirst();

            if (pendiente.isEmpty()) {
                return ResponseEntity.badRequest().body("El usuario no tiene un rol pendiente.");
            }

            Rol rol = pendiente.get();
            rol.setNombreRol("RECHAZADO");
            rolRepo.save(rol);

            usuario.setEstado("BLOQUEADO");
            usuarioRepo.save(usuario);

            return ResponseEntity.ok("Usuario rechazado y bloqueado.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }

    public ResponseEntity<?> actualizar(Long id, Usuarios usuarioActualizado) {
        return usuarioRepo.findById(id).map(usuario -> {
            if (usuarioActualizado.getNombre() != null)
                usuario.setNombre(usuarioActualizado.getNombre());
            if (usuarioActualizado.getEmail() != null)
                usuario.setEmail(usuarioActualizado.getEmail());
            if (usuarioActualizado.getPassword() != null)
                usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
            if (usuarioActualizado.getFechaNacimiento() != null)
                usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());

            usuarioRepo.save(usuario);
            return ResponseEntity.ok("Usuario actualizado correctamente.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }

    public ResponseEntity<?> actualizarFechaNacimiento(Long id, String nuevaFecha) {
        return usuarioRepo.findById(id).map(usuario -> {
            try {
                LocalDate fecha = LocalDate.parse(nuevaFecha);
                usuario.setFechaNacimiento(fecha);
                usuarioRepo.save(usuario);
                return ResponseEntity.ok("Fecha de nacimiento actualizada correctamente.");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Formato inválido. Usa yyyy-MM-dd.");
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }

    public ResponseEntity<?> eliminarUsuarioAdmin(Long idAdmin, Long idUsuario) {
        if (!esAdminValido(idAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un administrador puede eliminar usuarios.");
        }

        return usuarioRepo.findById(idUsuario).map(usuario -> {
            usuario.setEstado("ELIMINADO");
            usuarioRepo.save(usuario);
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }

    public List<Usuarios> listarTodos() {
        return usuarioRepo.findAll();
    }

    public List<Usuarios> filtrarPorInicial(String letra) {
        return usuarioRepo.findAll().stream()
                .filter(u -> u.getNombreUsuario() != null &&
                        u.getNombreUsuario().toLowerCase().startsWith(letra.toLowerCase()))
                .toList();
    }

    public ResponseEntity<Usuarios> obtenerPorId(Long id) {
        return usuarioRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public List<Rol> listarRolesPorUsuario(Long id) {
        return usuarioRepo.findById(id)
                .map(Usuarios::getRoles)
                .orElse(Collections.emptyList());
    }

    public ResponseEntity<?> login(String email, String password) {
        Usuarios usuario = usuarioRepo.findByEmail(email);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", usuario.getIdUsuario());
        respuesta.put("nombreUsuario", usuario.getNombreUsuario());
        respuesta.put("roles", usuario.getRoles().stream()
                .map(Rol::getNombreRol)
                .toList());

        return ResponseEntity.ok(respuesta);
    }

    public boolean estaActivo(Long idUsuario) {
        return usuarioRepo.findById(idUsuario)
                .map(u -> "ACTIVO".equalsIgnoreCase(u.getEstado()))
                .orElse(false);
    }

    public String obtenerRolPrincipal(Long idUsuario) {
        return rolRepo.findByUsuario_IdUsuario(idUsuario).stream()
                .findFirst()
                .map(Rol::getNombreRol)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró un rol asociado al usuario."));
    }
}