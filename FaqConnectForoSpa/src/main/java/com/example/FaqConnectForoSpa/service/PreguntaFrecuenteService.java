package com.example.FaqConnectForoSpa.service;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import com.example.FaqConnectForoSpa.repository.PreguntaFrecuenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreguntaFrecuenteService {

    private final PreguntaFrecuenteRepository repository;

    public List<PreguntaFrecuente> getAll() {
        return repository.findAll();
    }

    public PreguntaFrecuente getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PreguntaFrecuente save(PreguntaFrecuente pregunta) {
        return repository.save(pregunta);
    }

    public PreguntaFrecuente update(Long id, PreguntaFrecuente pregunta) {
        PreguntaFrecuente existente = repository.findById(id).orElseThrow();
        existente.setPregunta(pregunta.getPregunta());
        existente.setRespuesta(pregunta.getRespuesta());
        return repository.save(existente);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Nuevo método para filtrar por palabra clave
    public List<PreguntaFrecuente> filtrarPorPalabraClave(String palabraClave) {
        return repository.findByPreguntaContainingIgnoreCase(palabraClave);
    }
}
