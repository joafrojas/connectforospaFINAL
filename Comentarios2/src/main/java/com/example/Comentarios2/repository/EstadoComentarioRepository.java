package com.example.Comentarios2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Comentarios2.model.EstadoComentario;

@Repository
public interface EstadoComentarioRepository extends JpaRepository<EstadoComentario, Long> {
    Optional<EstadoComentario> findByNombreEstado(String nombreEstado);
}
