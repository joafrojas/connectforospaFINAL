package com.example.SoporteTecnico.service;

import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.repository.SoporteTecnicoRepository;
import com.example.SoporteTecnico.webClient.UsuariosClient;
import com.example.SoporteTecnico.webClient.dto.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
public class SoporteTecnicoService {

    private final SoporteTecnicoRepository soporteTecnicoRepository;
    private final UsuariosClient usuariosClient;

    public SoporteTecnicoService(SoporteTecnicoRepository soporteTecnicoRepository,
                                  UsuariosClient usuariosClient) {
        this.soporteTecnicoRepository = soporteTecnicoRepository;
        this.usuariosClient = usuariosClient;
    }

    public List<SoporteTecnico> getAll() {
        return soporteTecnicoRepository.findAll();
    }

    public SoporteTecnico save(SoporteTecnico soporteTecnico) {
        Long idUsuario = soporteTecnico.getIdUsuario();

        Usuario usuario = usuariosClient.obtenerUsuarioPorId(idUsuario).block();
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se pudo registrar el soporte: el usuario con ID " + idUsuario + " no existe."
            );
        }

        Boolean tienePermiso = usuariosClient.tieneRolSoporte(idUsuario).block();
        if (Boolean.FALSE.equals(tienePermiso)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario con ID " + idUsuario + " no tiene el rol necesario para acceder al módulo de soporte técnico."
            );
        }

        return soporteTecnicoRepository.save(soporteTecnico);
    }

    public void delete(Long idSoporteTecnico) {
        if (!soporteTecnicoRepository.existsById(idSoporteTecnico)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se puede eliminar: el soporte con ID " + idSoporteTecnico + " no existe en la base de datos."
            );
        }

        soporteTecnicoRepository.deleteById(idSoporteTecnico);
    }

    public List<SoporteTecnico> obtenerSoportePorUsuario(Long id) {
        List<SoporteTecnico> lista = soporteTecnicoRepository.findByIdUsuario(id);
        if (lista.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "No hay solicitudes de soporte registradas para el usuario con ID: " + id
            );
        }
        return lista;
    }

    public SoporteTecnico obtenerSoportePorId(Long id) {
        return soporteTecnicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Soporte técnico no encontrado para el ID: " + id
                ));
    }
}