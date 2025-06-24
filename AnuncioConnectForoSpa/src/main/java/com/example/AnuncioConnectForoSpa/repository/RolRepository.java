package com.example.AnuncioConnectForoSpa.repository;

import com.example.AnuncioConnectForoSpa.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Long> {
    List<Rol> findByUsuario_IdUsuario(Long idUsuario);
}