package com.example.Notificaciones2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Notificaciones2.model.Notificaciones;
import com.example.Notificaciones2.repository.NotificacionesRepository;
import com.example.Notificaciones2.webClient.UsuarioClient;
import com.example.Notificaciones2.webClient.dto.Usuario;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class NotificacionesService {


    private final NotificacionesRepository notificacionesRepository;
    private final UsuarioClient usuarioClient;


    @Autowired
    public NotificacionesService(NotificacionesRepository notificacionesRepository,
                                UsuarioClient usuarioClient){
        this.notificacionesRepository = notificacionesRepository;
        this.usuarioClient = usuarioClient; 
    }


    public List<Notificaciones> getAll() {
        return notificacionesRepository.findAll();
    }

    public Notificaciones save(Notificaciones notificaciones) {
    Long usuario = notificaciones.getIdUsuario();

    if (notificaciones.getMensaje() == null || notificaciones.getMensaje().isBlank()) {
        throw new IllegalArgumentException("El mensaje no puede ser nulo ni estar vacío");
    }

    Mono<Usuario> resultado = usuarioClient.obtenerUsuarioPorId(usuario);
    Usuario usuarioEncontrado = resultado.block();

    if (usuarioEncontrado == null) {
        throw new RuntimeException("Usuario no encontrado");
    }

    return notificacionesRepository.save(notificaciones);
}



    public void delete(Long idNotificacion){
        notificacionesRepository.deleteById(idNotificacion);
    }

    public  Notificaciones obtenerNotificacionPorId(Long id) {
        return notificacionesRepository.findById(id).orElse(null);
    }

    public List<Notificaciones> obtenerNotificacionPorUsuario(Long idUsuario) {
        return notificacionesRepository.findByIdUsuario(idUsuario);
    }
}
