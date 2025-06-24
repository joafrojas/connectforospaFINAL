package connectforospa_usuarios.repository;

import connectforospa_usuarios.model.Rol;
import connectforospa_usuarios.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegistroUsuarioRepository extends JpaRepository<Usuarios, Long> {
    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByEmail(String email);

    Usuarios findByEmail(String email);

    

}
