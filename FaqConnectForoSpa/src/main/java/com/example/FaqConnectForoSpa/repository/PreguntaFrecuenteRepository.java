package com.example.FaqConnectForoSpa.repository;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaFrecuenteRepository extends JpaRepository<PreguntaFrecuente, Long> {
    List<PreguntaFrecuente> findByPreguntaContainingIgnoreCase(String palabraClave);
}
