package connectforospa_foro.service;

import connectforospa_foro.model.Foro;
import connectforospa_foro.repository.ForoRepository;
import connectforospa_foro.webclient.UsuarioClient;
import connectforospa_foro.webclient.UsuarioDTO;
import connectforospa_foro.webclient.RolDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForoService {

    private final ForoRepository foroRepo;
    private final UsuarioClient usuarioClient;

    public ResponseEntity<String> crearForo(Foro foro, Long idUsuario) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerUsuario(idUsuario);
            if (usuario == null)
                return ResponseEntity.status(404).body("Usuario no encontrado.");

            if ("ELIMINADO".equalsIgnoreCase(usuario.getEstado()))
                return ResponseEntity.status(403).body("Usuario eliminado, no puede crear foros.");

            List<RolDTO> roles = usuario.getRoles();
            if (roles == null || roles.isEmpty())
                return ResponseEntity.status(403).body("Usuario sin roles asignados.");

            boolean autorizado = roles.stream().anyMatch(r ->
                "ROLE_ADMIN".equals(r.getNombreRol()) ||
                "ROLE_MODERADOR".equals(r.getNombreRol()) ||
                "ROLE_USER".equals(r.getNombreRol())
            );

            if (!autorizado)
                return ResponseEntity.status(403).body("Rol no autorizado para crear foros.");

            if (foro.getNombreForo() == null || foro.getNombreForo().isBlank())
                return ResponseEntity.badRequest().body("El nombre del foro no puede estar vacío.");

            foro.setIdUsuario(idUsuario);
            foroRepo.save(foro);

            return ResponseEntity.status(201).body("Foro creado exitosamente.");

        } catch (Exception e) {
            log.error("Error en crearForo():", e);
            return ResponseEntity.status(500).body("Error interno del servidor.");
        }
    }

    public List<Foro> listar() {
        return foroRepo.findAll();
    }

    public Foro obtenerPorId(Long id) {
        return foroRepo.findById(id).orElse(null);
    }

    public List<Foro> listarPorUsuario(Long idUsuario) {
        return foroRepo.findByIdUsuario(idUsuario);
    }

    public List<Foro> buscar(String nombreForo) {
        return foroRepo.findByNombreForoContainingIgnoreCase(nombreForo);
    }

    public ResponseEntity<String> eliminar(Long idForo, Long idUsuario) {
        return foroRepo.findById(idForo).map(foro -> {
            if (!foro.getIdUsuario().equals(idUsuario)) {
                return ResponseEntity.status(403).body("No autorizado para eliminar este foro.");
            }
            foroRepo.deleteById(idForo);
            return ResponseEntity.ok("Foro eliminado con éxito.");
        }).orElse(ResponseEntity.status(404).body("Foro no encontrado."));
    }

    public ResponseEntity<String> modificar(Long idForo, Long idUsuario, Foro foro) {
        return foroRepo.findById(idForo).map(existing -> {
            if (!existing.getIdUsuario().equals(idUsuario)) {
                return ResponseEntity.status(403).body("No autorizado para modificar este foro.");
            }
            if (foro.getNombreForo() != null && !foro.getNombreForo().isBlank()) {
                existing.setNombreForo(foro.getNombreForo());
                foroRepo.save(existing);
            }
            return ResponseEntity.ok("Foro actualizado correctamente.");
        }).orElse(ResponseEntity.status(404).body("Foro no encontrado."));
    }
}