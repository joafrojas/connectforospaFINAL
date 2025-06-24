package connectforospa_gestiondecategorias.service;

import connectforospa_gestiondecategorias.model.Categorias;
import connectforospa_gestiondecategorias.repository.GestionDeCategoriasRepository;
import connectforospa_gestiondecategorias.webclient.RolDTO;
import connectforospa_gestiondecategorias.webclient.UsuarioClient;
import connectforospa_gestiondecategorias.webclient.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionDeCategoriasService {

    private final GestionDeCategoriasRepository repo;
    private final UsuarioClient usuarioClient;

    public boolean puedeGestionarCategorias(Long idUsuario) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerUsuario(idUsuario).block();
            return usuario != null &&
                    usuario.getRoles() != null &&
                    usuario.getRoles().stream()
                            .map(RolDTO::getNombreRol)
                            .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_MODERADOR"));
        } catch (Exception e) {
            log.warn("No se pudo verificar el rol del usuario {}: {}", idUsuario, e.getMessage());
            return false;
        }
    }

    public ResponseEntity<?> guardar(Categorias categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(" El nombre de la categoría es obligatorio.");
        }

        if (repo.findByNombreIgnoreCase(categoria.getNombre()).isPresent()) {
            return ResponseEntity.status(409).body("Ya existe una categoría con ese nombre.");
        }

        Categorias guardada = repo.save(categoria);
        return ResponseEntity.status(201).body(" Categoría creada: " + guardada.getNombre());
    }

    public List<Categorias> listarTodas() {
        return repo.findAll();
    }

    public Optional<Categorias> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public ResponseEntity<?> actualizar(Long id, Categorias actualizada) {
        return repo.findById(id).map(cat -> {
            if (actualizada.getNombre() != null && !actualizada.getNombre().isBlank()) {
                Optional<Categorias> existente = repo.findByNombreIgnoreCase(actualizada.getNombre());
                if (existente.isPresent() && !existente.get().getId().equals(id)) {
                    return ResponseEntity.status(409).body(" Ya existe otra categoría con ese nombre.");
                }
                cat.setNombre(actualizada.getNombre());
            }
            if (actualizada.getDescripcion() != null) {
                cat.setDescripcion(actualizada.getDescripcion());
            }
            repo.save(cat);
            return ResponseEntity.ok(" Categoría actualizada: " + cat.getNombre());
        }).orElse(ResponseEntity.status(404).body(" Categoría no encontrada con ID: " + id));
    }

    public ResponseEntity<String> actualizarNombre(Long id, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            return ResponseEntity.badRequest().body(" El nombre de la categoría no puede estar vacío.");
        }

        return repo.findById(id).map(cat -> {
            cat.setNombre(nuevoNombre);
            repo.save(cat);
            return ResponseEntity.ok(" Nombre de la categoría actualizado: " + cat.getNombre());
        }).orElse(ResponseEntity.status(404).body(" Categoría no encontrada con ID: " + id));
    }

}