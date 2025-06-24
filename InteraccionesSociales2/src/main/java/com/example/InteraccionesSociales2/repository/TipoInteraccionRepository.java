package com.example.InteraccionesSociales2.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.InteraccionesSociales2.model.TipoInteraccion;

@Repository
public interface TipoInteraccionRepository extends JpaRepository<TipoInteraccion, Long> {
    Optional<TipoInteraccion> findByNombre(String nombre);


}
