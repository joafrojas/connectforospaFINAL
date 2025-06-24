package com.example.Comentarios2.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Comentarios2.model.Comentarios;

@Repository
public interface ComentariosRepository extends JpaRepository<Comentarios, Long>{
    List<Comentarios> findByIdUsuario(long idUsuario);
    List<Comentarios> findByIdPublicacion(long idPublicacion);

}
