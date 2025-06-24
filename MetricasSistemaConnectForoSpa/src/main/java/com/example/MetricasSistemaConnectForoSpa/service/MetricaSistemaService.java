package com.example.MetricasSistemaConnectForoSpa.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.MetricasSistemaConnectForoSpa.dto.MetricaRequest;
import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.model.TipoMetrica;
import com.example.MetricasSistemaConnectForoSpa.repository.MetricaSistemaRepository;
import com.example.MetricasSistemaConnectForoSpa.repository.TipoMetricaRepository;

@Service
public class MetricaSistemaService {

    @Autowired
    private MetricaSistemaRepository metricaRepository;

    @Autowired
    private TipoMetricaRepository tipoRepository;

    public MetricaSistema registrar(MetricaRequest request) {
        TipoMetrica tipo = tipoRepository.findById(request.getIdTipo())
                .orElseThrow(() -> new RuntimeException("Tipo metrica no encontrado"));

        MetricaSistema metrica = MetricaSistema.builder()
                .descripcion(request.getDescripcion())
                .fecha_registro(LocalDateTime.now())
                .tipo(tipo)
                .build();

        return metricaRepository.save(metrica);
    }

    public List<MetricaSistema> obtenerPorTipo(Long idTipo) {
        TipoMetrica tipo = tipoRepository.findById(idTipo)
                .orElseThrow(() -> new RuntimeException("Tipo de metrica no encontrada"));

        return metricaRepository.findByTipo(tipo);
    }

    public List<MetricaSistema> obtenerTodas() {
        return metricaRepository.findAll();
    }

    public MetricaSistema actualizar(Long id, MetricaRequest request) {
        MetricaSistema metrica = metricaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metrica no encontrada"));

        TipoMetrica tipo = tipoRepository.findById(request.getIdTipo())
                .orElseThrow(() -> new RuntimeException("Tipo de metrica no encontrada"));

        metrica.setDescripcion(request.getDescripcion());
        metrica.setTipo(tipo);

        return metricaRepository.save(metrica);
    }

    public MetricaSistema actualizarDescripcion(Long id, String nuevaDescripcion) {
        MetricaSistema metrica = metricaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metrica no encontrada"));

        metrica.setDescripcion(nuevaDescripcion);
        return metricaRepository.save(metrica);
    }

    public void eliminar(Long id) {
        metricaRepository.deleteById(id);
    }

}
