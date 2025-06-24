package com.example.AnuncioConnectForoSpa.repository;

import com.example.AnuncioConnectForoSpa.model.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByFechaPublicacionAfter(LocalDateTime fecha);
    List<Anuncio> findByPrioridad(String prioridad);
}
