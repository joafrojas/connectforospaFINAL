package com.example.AnuncioConnectForoSpa.service;

import com.example.AnuncioConnectForoSpa.dto.AnuncioRequest;
import com.example.AnuncioConnectForoSpa.dto.AnuncioResponse;
import com.example.AnuncioConnectForoSpa.model.Anuncio;
import com.example.AnuncioConnectForoSpa.model.Estado;
import com.example.AnuncioConnectForoSpa.repository.AnuncioRepository;
import com.example.AnuncioConnectForoSpa.repository.EstadoRepository;
import com.example.AnuncioConnectForoSpa.webclient.UsuarioClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnuncioService {

    private final AnuncioRepository repository;
    private final EstadoRepository estadoRepository;
    private final UsuarioClient usuarioClient;

    public AnuncioService(AnuncioRepository repository,
                          EstadoRepository estadoRepository,
                          UsuarioClient usuarioClient) {
        this.repository = repository;
        this.estadoRepository = estadoRepository;
        this.usuarioClient = usuarioClient;
    }

    public List<AnuncioResponse> getAll() {
        return repository.findAll().stream()
                .map(a -> new AnuncioResponse(
                        a.getIdAnuncio(),
                        a.getTitulo(),
                        a.getFechaPublicacion(),
                        a.getPrioridad(),
                        a.getEstado().getNombre(),
                        "Desconocido"
                ))
                .collect(Collectors.toList());
    }

    public Anuncio create(AnuncioRequest request) {
        boolean tienePermiso = usuarioClient.tieneRolAnuncios(request.getIdUsuario()).block();
        if (!tienePermiso) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no tiene permiso para publicar anuncios.");
        }

        Estado estado = estadoRepository.findById(request.getIdEstado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no encontrado."));

        Anuncio anuncio = Anuncio.builder()
                .titulo(request.getTitulo())
                .foto(request.getFoto())
                .fechaPublicacion(LocalDateTime.now())
                .prioridad(request.getPrioridad())
                .usuarioId(request.getIdUsuario())
                .estado(estado)
                .build();

        return repository.save(anuncio);
    }

    public Anuncio update(Long id, AnuncioRequest request) {
        Anuncio anuncio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado."));

        Estado estado = estadoRepository.findById(request.getIdEstado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no encontrado."));

        anuncio.setTitulo(request.getTitulo());
        anuncio.setFoto(request.getFoto());
        anuncio.setPrioridad(request.getPrioridad());
        anuncio.setUsuarioId(request.getIdUsuario());
        anuncio.setEstado(estado);

        return repository.save(anuncio);
    }

    public Anuncio updatePrioridad(Long id, String nuevaPrioridad) {
        Anuncio anuncio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio no encontrado."));
        anuncio.setPrioridad(nuevaPrioridad);
        return repository.save(anuncio);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede eliminar: anuncio no encontrado.");
        }
        repository.deleteById(id);
    }

    public List<Anuncio> getByFecha(LocalDateTime desde) {
        return repository.findByFechaPublicacionAfter(desde);
    }

    public List<Anuncio> getPorPrioridad(String prioridad) {
        return repository.findByPrioridad(prioridad);
    }
}