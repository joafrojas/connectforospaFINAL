package connectforospa_foro.repository;

import connectforospa_foro.model.Foro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForoRepository extends JpaRepository<Foro, Long> {
    List<Foro> findByNombreForoContainingIgnoreCase(String nombreForo);
    List<Foro> findByIdUsuario(Long idUsuario);
}