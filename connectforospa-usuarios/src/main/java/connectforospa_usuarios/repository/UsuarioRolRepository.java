package connectforospa_usuarios.repository;

import connectforospa_usuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<Rol, Long> {
    List<Rol> findByUsuario_IdUsuario(Long idUsuario);
}