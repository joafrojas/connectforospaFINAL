package connectforospa_publicacion.repository;

import connectforospa_publicacion.model.EstadoPublicacion;
import connectforospa_publicacion.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByIdUsuario(Long idUsuario);

    List<Publicacion> findByIdCategoria(Long idCategoria);

    List<Publicacion> findByEstadoPublicacion(EstadoPublicacion estado);
}