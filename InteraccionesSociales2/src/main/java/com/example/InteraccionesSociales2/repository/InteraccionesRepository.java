package com.example.InteraccionesSociales2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.InteraccionesSociales2.model.Interacciones;
import com.example.InteraccionesSociales2.model.TipoInteraccion;
import java.util.List;

public interface InteraccionesRepository extends JpaRepository<Interacciones, Long> {
    boolean existsByIdUsuarioAndIdPublicacionAndTipoInteraccion(Long idUsuario, Long idPublicacion, TipoInteraccion tipoInteraccion);

    List<Interacciones> findByIdUsuario(Long idUsuario);

    List<Interacciones> findByIdPublicacion(Long idPublicacion);
}