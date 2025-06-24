package connectforospa_publicacion.service;

import connectforospa_publicacion.model.EstadoPublicacion;
import connectforospa_publicacion.model.Publicacion;
import connectforospa_publicacion.repository.PublicacionRepository;
import connectforospa_publicacion.webclient.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicacionService {

    private final PublicacionRepository publicacionRepo;
    private final UsuarioClient usuarioClient;
    private final CategoriaClient categoriaClient;
    private final ForoClient foroClient;

    public Mono<Publicacion> guardarPublicacion(Publicacion publicacion) {
        Long idUsuario = publicacion.getIdUsuario();

        return usuarioClient.obtenerUsuario(idUsuario)
                .filter(usuario -> usuario != null &&
                        !"ELIMINADO".equalsIgnoreCase(usuario.getEstado()) &&
                        tieneRolValido(usuario))
                .flatMap(usuario -> {
                    EstadoPublicacion estado = esAdminOModerador(usuario)
                            ? EstadoPublicacion.APROBADA
                            : EstadoPublicacion.PENDIENTE;
                    publicacion.setEstadoPublicacion(estado);

                    return categoriaClient.validarCategoria(publicacion.getIdCategoria())
                            .filter(Boolean::booleanValue)
                            .flatMap(ignored ->
                                    foroClient.validarForo(publicacion.getIdForo())
                                            .filter(Boolean::booleanValue)
                                            .map(__ -> {
                                                publicacion.setFechaPublicacion(LocalDateTime.now());
                                                return publicacionRepo.save(publicacion);
                                            })
                            );
                })
                .onErrorResume(e -> {
                    log.error("Error al guardar publicación", e);
                    return Mono.empty();
                });
    }

    private boolean tieneRolValido(UsuarioDTO usuario) {
        return usuario.getRoles() != null &&
                usuario.getRoles().stream()
                        .map(RolDTO::getNombreRol)
                        .anyMatch(rol -> rol.equals("ROLE_USER") ||
                                         rol.equals("ROLE_ADMIN") ||
                                         rol.equals("ROLE_MODERADOR"));
    }

    private boolean esAdminOModerador(UsuarioDTO usuario) {
        return usuario.getRoles() != null &&
                usuario.getRoles().stream()
                        .map(RolDTO::getNombreRol)
                        .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_MODERADOR"));
    }

    public boolean esAdminOModerador(Long idUsuario) {
        return usuarioClient.obtenerUsuario(idUsuario)
                .map(this::esAdminOModerador)
                .defaultIfEmpty(false)
                .block();
    }

    public Publicacion moderarPublicacion(Long idPublicacion, Long idModerador, String accion) {
        if (!esAdminOModerador(idModerador)) return null;

        return publicacionRepo.findById(idPublicacion)
                .map(pub -> {
                    switch (accion.toUpperCase()) {
                        case "APROBAR" -> pub.setEstadoPublicacion(EstadoPublicacion.APROBADA);
                        case "RECHAZAR" -> pub.setEstadoPublicacion(EstadoPublicacion.RECHAZADA);
                        default -> throw new IllegalArgumentException("Acción inválida: " + accion);
                    }
                    return publicacionRepo.save(pub);
                })
                .orElse(null);
    }

    public boolean eliminarComoModerador(Long idPublicacion, Long idModerador) {
        if (!esAdminOModerador(idModerador)) return false;
        publicacionRepo.deleteById(idPublicacion);
        return true;
    }

    public List<Publicacion> obtenerPublicacionesPorUsuario(Long idUsuario) {
        return publicacionRepo.findByIdUsuario(idUsuario);
    }

    public List<Publicacion> listarPublicacionesAprobadas() {
        return publicacionRepo.findByEstadoPublicacion(EstadoPublicacion.APROBADA);
    }

    public Publicacion obtenerPorId(Long id) {
        return publicacionRepo.findById(id).orElse(null);
    }

    public List<Publicacion> listarPorCategoria(Long idCategoria) {
        return publicacionRepo.findByIdCategoria(idCategoria);
    }
    public Publicacion actualizarPublicacion(Long id, Publicacion publicacion) {
        return publicacionRepo.findById(id)
                .map(existingPub -> {
                    existingPub.setTitulo(publicacion.getTitulo());
                    existingPub.setContenido(publicacion.getContenido());
                    return publicacionRepo.save(existingPub);
                })
                .orElse(null);
    }
}