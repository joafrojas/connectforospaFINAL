package com.example.SoporteTecnico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SoporteTecnico.model.Respuesta;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByIdSoporteTecnico_IdSoporte(Long idSoporte);


}
