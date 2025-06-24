package com.example.InteraccionesSociales2.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.InteraccionesSociales2.model.Interacciones;
import com.example.InteraccionesSociales2.model.TipoInteraccion;
import com.example.InteraccionesSociales2.repository.InteraccionesRepository;
import com.example.InteraccionesSociales2.repository.TipoInteraccionRepository;
import com.example.InteraccionesSociales2.webClient.ForosClient;
import com.example.InteraccionesSociales2.webClient.PublicacionesClient;
import com.example.InteraccionesSociales2.webClient.UsuariosClient;
import com.example.InteraccionesSociales2.webClient.dto.Foros;
import com.example.InteraccionesSociales2.webClient.dto.Publicacion;
import com.example.InteraccionesSociales2.webClient.dto.Usuario;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InteraccionesService {

    private final InteraccionesRepository interaccionesRepository;
    private final PublicacionesClient publicacionesClient;
    private final UsuariosClient usuariosClient;
    private final TipoInteraccionRepository tipoInteraccionRepository;
    private final ForosClient forosClient;

    public InteraccionesService(InteraccionesRepository interaccionesRepository,
                                 PublicacionesClient publicacionesClient,
                                 UsuariosClient usuariosClient,
                                 TipoInteraccionRepository tipoInteraccionRepository,
                                 ForosClient forosClient) {
        this.interaccionesRepository = interaccionesRepository;
        this.publicacionesClient = publicacionesClient;
        this.usuariosClient = usuariosClient;
        this.tipoInteraccionRepository = tipoInteraccionRepository;
        this.forosClient = forosClient;
    }

    public List<Interacciones> getAll() {
        return interaccionesRepository.findAll();
    }

    public Interacciones save(Interacciones interaccion) {
        Long idUsuario = interaccion.getIdUsuario();
        Long idPublicacion = interaccion.getIdPublicacion();
        Long idForo = interaccion.getIdForo();
        String nombreTipo = interaccion.getTipoInteraccion() != null
                ? interaccion.getTipoInteraccion().getNombre()
                : null;

        if (idUsuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe proporcionar el ID del usuario.");
        }
        if (idPublicacion == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe proporcionar el ID de la publicación.");
        }
        if (idForo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe proporcionar el ID del foro.");
        }
        if (nombreTipo == null || nombreTipo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el nombre del tipo de interacción.");
        }

        Usuario usuario = usuariosClient.obtenerUsuarioPorId(idUsuario, "SIN_TOKEN").block();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario con ID: " + idUsuario);
        }

        Publicacion publicacion = publicacionesClient.obtenerPorId(idPublicacion).block();
        if (publicacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la publicación con ID: " + idPublicacion);
        }

        Foros foro = forosClient.obtenerPorId(idForo).block();
        if (foro == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el foro con ID: " + idForo);
        }

        TipoInteraccion tipo = tipoInteraccionRepository.findByNombre(nombreTipo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tipo de interacción '" + nombreTipo + "' no existe."));

        boolean yaExiste = interaccionesRepository.existsByIdUsuarioAndIdPublicacionAndTipoInteraccion(
                idUsuario, idPublicacion, tipo);

        if (yaExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este usuario ya realizó esta interacción en esa publicación.");
        }

        interaccion.setTipoInteraccion(tipo);
        return interaccionesRepository.save(interaccion);
    }

    public Interacciones obtenerInteraccionPorId(Long id) {
        return interaccionesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la interacción con ID: " + id));
    }

    public List<Interacciones> obtenerInteraccionesPorUsuario(Long idUsuario) {
        return interaccionesRepository.findByIdUsuario(idUsuario);
    }

    public List<Interacciones> obtenerInteraccionPorPublicacion(Long idPublicacion) {
        return interaccionesRepository.findByIdPublicacion(idPublicacion);
    }

    public void deletePorId(Long id) {
        if (!interaccionesRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se puede eliminar: la interacción con ID " + id + " no existe.");
        }
        interaccionesRepository.deleteById(id);
    }
}