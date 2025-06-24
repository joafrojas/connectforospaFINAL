package com.example.SoporteTecnico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SoporteTecnico.model.MotivoSoporte;


@Repository
public interface MotivoSoporteRepository extends JpaRepository<MotivoSoporte, Long> {
    Optional<MotivoSoporte> findByNombreMotivo(String nombreMotivo);

}
