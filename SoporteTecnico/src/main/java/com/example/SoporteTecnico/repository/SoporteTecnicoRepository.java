package com.example.SoporteTecnico.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SoporteTecnico.model.SoporteTecnico;

@Repository
public interface SoporteTecnicoRepository extends JpaRepository<SoporteTecnico, Long>{
    List<SoporteTecnico> findByIdUsuario(long idUsuario);
}
