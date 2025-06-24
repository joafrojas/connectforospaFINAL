package com.example.SoporteTecnico.service;

import com.example.SoporteTecnico.model.Respuesta;
import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.repository.RespuestaRepository;
import com.example.SoporteTecnico.repository.SoporteTecnicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final SoporteTecnicoRepository soporteTecnicoRepository;

    public RespuestaService(RespuestaRepository respuestaRepository,
                            SoporteTecnicoRepository soporteTecnicoRepository) {
        this.respuestaRepository = respuestaRepository;
        this.soporteTecnicoRepository = soporteTecnicoRepository;
    }

    public Respuesta crearRespuesta(Long idSoporte, String descripcion) {
        SoporteTecnico soporte = soporteTecnicoRepository.findById(idSoporte)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró una solicitud de soporte técnico con ID: " + idSoporte
            ));

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes ingresar una descripción para la respuesta. No puede estar vacía."
            );
        }

        Respuesta respuesta = new Respuesta();
        respuesta.setIdSoporteTecnico(soporte);
        respuesta.setDescripcion(descripcion.trim());
        respuesta.setFechaRespuesta(LocalDateTime.now());

        return respuestaRepository.save(respuesta);
    }

    public List<Respuesta> obtenerRespuestasPorSoporte(Long idSoporte) {
        if (!soporteTecnicoRepository.existsById(idSoporte)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe ningún soporte técnico con ID: " + idSoporte
            );
        }

        List<Respuesta> respuestas = respuestaRepository.findByIdSoporteTecnico_IdSoporte(idSoporte);

        if (respuestas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "Actualmente no hay respuestas asociadas al soporte técnico con ID: " + idSoporte
            );
        }

        return respuestas;
    }
}