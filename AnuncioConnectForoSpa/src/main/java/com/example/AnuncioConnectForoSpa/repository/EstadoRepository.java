package com.example.AnuncioConnectForoSpa.repository;

import com.example.AnuncioConnectForoSpa.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
}
