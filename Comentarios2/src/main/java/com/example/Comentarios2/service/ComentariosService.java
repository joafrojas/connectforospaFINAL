package com.example.Comentarios2.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.Comentarios2.config.TokenUtils;
import com.example.Comentarios2.model.Comentarios;
import com.example.Comentarios2.model.EstadoComentario;
import com.example.Comentarios2.repository.ComentariosRepository;
import com.example.Comentarios2.repository.EstadoComentarioRepository;
import com.example.Comentarios2.webClient.ForosClient;
import com.example.Comentarios2.webClient.PublicacionesClient;
import com.example.Comentarios2.webClient.UsuariosClient;
import com.example.Comentarios2.webClient.dto.Foros;
import com.example.Comentarios2.webClient.dto.Publicacion;
import com.example.Comentarios2.webClient.dto.Usuario;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComentariosService {

    private final ComentariosRepository comentariosRepository;
    private final PublicacionesClient publicacionesClient;
    private final UsuariosClient usuariosClient;
    private final ForosClient forosClient;
    private final EstadoComentarioRepository estadoRepository;

    public ComentariosService(ComentariosRepository comentariosRepository,
            PublicacionesClient publicacionesClient,
            UsuariosClient usuariosClient,
            EstadoComentarioRepository estadoRepository,
            ForosClient forosClient) {
        this.comentariosRepository = comentariosRepository;
        this.publicacionesClient = publicacionesClient;
        this.usuariosClient = usuariosClient;
        this.estadoRepository = estadoRepository;
        this.forosClient = forosClient;
    }

    public List<Comentarios> getAll() {
        return comentariosRepository.findAll();
    }

    public Comentarios save(Comentarios comentarios) {
        if (comentarios.getIdUsuario() == null || comentarios.getIdPublicacion() == null
                || comentarios.getIdForo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan usuario, publicación o foro");
        }

        String token = TokenUtils.extraerTokenDelContexto();
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no presente");
        }

        System.out.println("[DEBUG] Token: " + token);
        System.out.println("[DEBUG] idUsuario: " + comentarios.getIdUsuario());
        System.out.println("[DEBUG] idPublicacion: " + comentarios.getIdPublicacion());
        System.out.println("[DEBUG] idForo: " + comentarios.getIdForo());

        Usuario usuario = usuariosClient.obtenerUsuarioPorId(comentarios.getIdUsuario(), token).block();
        Publicacion publicacion = publicacionesClient.obtenerPublicacionPorId(comentarios.getIdPublicacion()).block();
        Foros foro = forosClient.obtenerForoPorId(comentarios.getIdForo()).block();

        System.out.println("[DEBUG] usuario: " + (usuario != null ? usuario.getIdUsuario() : "null"));
        System.out.println("[DEBUG] publicacion: " + (publicacion != null ? publicacion.getIdPublicacion() : "null"));
        System.out.println("[DEBUG] foro: " + (foro != null ? foro.getIdForo() : "null"));

        if (usuario == null || publicacion == null || foro == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario, publicación o foro no encontrado");
        }

        if (comentarios.getEstadoComentario() == null || comentarios.getEstadoComentario().getNombreEstado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado del comentario es obligatorio");
        }

        String nombreEstado = comentarios.getEstadoComentario().getNombreEstado().toUpperCase();
        EstadoComentario estado = estadoRepository.findByNombreEstado(nombreEstado)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no válido: " + nombreEstado));

        comentarios.setEstadoComentario(estado);

        if (comentarios.getFechaPublicacion() == null) {
            comentarios.setFechaPublicacion(LocalDateTime.now());
        }

        return comentariosRepository.save(comentarios);
    }

    public void deletePorId(Long idComentario) {
        if (!comentariosRepository.existsById(idComentario)) {
            throw new EntityNotFoundException("Comentario no encontrado");
        }
        comentariosRepository.deleteById(idComentario);
    }

    public Comentarios obtenerComentarioPorId(Long id) {
        return comentariosRepository.findById(id).orElse(null);
    }

    public List<Comentarios> obtenerComentarioPorUsuario(Long idUsuario) {
        return comentariosRepository.findByIdUsuario(idUsuario);
    }

    public List<Comentarios> obtenerComentarioPorPublicacion(Long idPublicacion) {
        return comentariosRepository.findByIdPublicacion(idPublicacion);
    }

    public List<Comentarios> getUsuario(Long idUsuario) {
        return comentariosRepository.findByIdUsuario(idUsuario);
    }
}