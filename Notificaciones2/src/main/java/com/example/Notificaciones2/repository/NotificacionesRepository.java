package com.example.Notificaciones2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Notificaciones2.model.Notificaciones;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Long>{
    List<Notificaciones> findByIdUsuario(long idUsuario);
    
}
