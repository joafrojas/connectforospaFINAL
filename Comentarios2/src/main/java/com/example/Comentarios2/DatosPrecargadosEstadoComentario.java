package com.example.Comentarios2;

import org.springframework.stereotype.Component;

import com.example.Comentarios2.model.EstadoComentario;
import com.example.Comentarios2.repository.EstadoComentarioRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DatosPrecargadosEstadoComentario {

    private final EstadoComentarioRepository estadoComentarioRepository;

    public DatosPrecargadosEstadoComentario(EstadoComentarioRepository estadoComentarioRepository) {
    this.estadoComentarioRepository = estadoComentarioRepository;
    }


    @PostConstruct
    public void init() {
        if (estadoComentarioRepository.count() == 0) {
            estadoComentarioRepository.save(new EstadoComentario(1L, "ACTIVO"));
            estadoComentarioRepository.save(new EstadoComentario(2L, "INACTIVO"));
            estadoComentarioRepository.save(new EstadoComentario(3L, "SUSPENDIDO"));
            System.out.println("✔ Tipos de interacción precargados correctamente.");
        }
    }
}
