package com.example.MetricasSistemaConnectForoSpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.example.MetricasSistemaConnectForoSpa.dto.MetricaRequest;
import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.model.TipoMetrica;
import com.example.MetricasSistemaConnectForoSpa.repository.MetricaSistemaRepository;
import com.example.MetricasSistemaConnectForoSpa.repository.TipoMetricaRepository;

@ExtendWith(MockitoExtension.class)
public class MetricaSistemaServiceTest {

    @Mock
    private MetricaSistemaRepository metricaRepository;

    @Mock
    private TipoMetricaRepository tipoRepository;

    @InjectMocks
    private MetricaSistemaService service;

    @Test
    void registrar_retornaMetricaGuardada() {
        // Datos de entrada
        MetricaRequest request = new MetricaRequest("Uso alto de CPU", 1L);
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");

        // Fecha fija para test
        LocalDateTime fechaFija = LocalDateTime.of(2025, 6, 20, 12, 0);

        // Simular tipo encontrado
        when(tipoRepository.findById(1L)).thenReturn(java.util.Optional.of(tipo));

        // Metrica esperada al guardar
        MetricaSistema metricaMock = new MetricaSistema(1L, "Uso alto de CPU", fechaFija, tipo);
        when(metricaRepository.save(org.mockito.ArgumentMatchers.any(MetricaSistema.class)))
                .thenReturn(metricaMock);

        // Ejecutar
        MetricaSistema resultado = service.registrar(request);

        // Verificar
        assertThat(resultado.getDescripcion()).isEqualTo("Uso alto de CPU");
        assertThat(resultado.getTipo().getNombre()).isEqualTo("RENDIMIENTO");
        assertThat(resultado.getFecha_registro()).isEqualTo(fechaFija);
    }

    @Test
    void obtenerTodas_retornaListaDeMetricas() {
        // Datos simulados
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");
        MetricaSistema metrica1 = new MetricaSistema(1L, "Prueba 1", LocalDateTime.now(), tipo);
        MetricaSistema metrica2 = new MetricaSistema(2L, "Prueba 2", LocalDateTime.now(), tipo);

        List<MetricaSistema> mockList = Arrays.asList(metrica1, metrica2);

        // Simular comportamiento del repositorio
        when(metricaRepository.findAll()).thenReturn(mockList);

        // Ejecutar método
        List<MetricaSistema> resultado = service.obtenerTodas();

        // Verificar
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Prueba 1");
        assertThat(resultado.get(1).getDescripcion()).isEqualTo("Prueba 2");
    }

    @Test
    void obtenerPorTipo_retornaListaFiltrada() {
        // Arrange
        Long tipoId = 1L;
        TipoMetrica tipo = new TipoMetrica(tipoId, "RENDIMIENTO");

        MetricaSistema metrica1 = new MetricaSistema(1L, "CPU alta", LocalDateTime.of(2025, 6, 20, 10, 0), tipo);
        MetricaSistema metrica2 = new MetricaSistema(2L, "Memoria alta", LocalDateTime.of(2025, 6, 20, 11, 0), tipo);

        when(tipoRepository.findById(tipoId)).thenReturn(java.util.Optional.of(tipo));
        when(metricaRepository.findByTipo(tipo)).thenReturn(List.of(metrica1, metrica2));

        // Act
        var resultado = service.obtenerPorTipo(tipoId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("CPU alta");
        assertThat(resultado.get(1).getDescripcion()).isEqualTo("Memoria alta");
    }

    @Test
    void obtenerTodas_retornaListaCompleta() {
        // Arrange
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");

        MetricaSistema metrica1 = new MetricaSistema(1L, "CPU alta", LocalDateTime.of(2025, 6, 20, 10, 0), tipo);
        MetricaSistema metrica2 = new MetricaSistema(2L, "Memoria alta", LocalDateTime.of(2025, 6, 20, 11, 0), tipo);

        when(metricaRepository.findAll()).thenReturn(List.of(metrica1, metrica2));

        // Act
        var resultado = service.obtenerTodas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("CPU alta");
        assertThat(resultado.get(1).getDescripcion()).isEqualTo("Memoria alta");
    }

    @Test
    void actualizar_actualizaDescripcionYTipo() {
        // Arrange
        Long id = 1L;
        MetricaRequest request = new MetricaRequest("Nueva descripción", 2L);

        TipoMetrica tipoOriginal = new TipoMetrica(1L, "RENDIMIENTO");
        TipoMetrica tipoNuevo = new TipoMetrica(2L, "LATENCIA");

        MetricaSistema metricaExistente = new MetricaSistema(id, "Antigua descripción", LocalDateTime.now(),
                tipoOriginal);

        when(metricaRepository.findById(id)).thenReturn(java.util.Optional.of(metricaExistente));
        when(tipoRepository.findById(2L)).thenReturn(java.util.Optional.of(tipoNuevo));
        when(metricaRepository.save(metricaExistente)).thenReturn(metricaExistente);

        // Act
        MetricaSistema resultado = service.actualizar(id, request);

        // Assert
        assertThat(resultado.getDescripcion()).isEqualTo("Nueva descripción");
        assertThat(resultado.getTipo().getNombre()).isEqualTo("LATENCIA");
    }

    @Test
    void actualizarDescripcion_actualizaSoloLaDescripcion() {
        // Arrange
        Long id = 1L;
        String nuevaDescripcion = "Descripción actualizada";
        TipoMetrica tipo = new TipoMetrica(1L, "RENDIMIENTO");

        MetricaSistema metricaExistente = new MetricaSistema(id, "Descripción antigua", LocalDateTime.now(), tipo);

        when(metricaRepository.findById(id)).thenReturn(java.util.Optional.of(metricaExistente));
        when(metricaRepository.save(metricaExistente)).thenReturn(metricaExistente);

        // Act
        MetricaSistema resultado = service.actualizarDescripcion(id, nuevaDescripcion);

        // Assert
        assertThat(resultado.getDescripcion()).isEqualTo("Descripción actualizada");
        assertThat(resultado.getTipo()).isEqualTo(tipo); // el tipo no debe cambiar
    }

    @Test
    void obtenerPorTipo_retornaMetricasDelTipo() {
        // Arrange
        Long idTipo = 1L;
        TipoMetrica tipo = new TipoMetrica(idTipo, "RENDIMIENTO");

        MetricaSistema metrica1 = new MetricaSistema(1L, "Carga alta", LocalDateTime.now(), tipo);
        MetricaSistema metrica2 = new MetricaSistema(2L, "Temperatura elevada", LocalDateTime.now(), tipo);

        when(tipoRepository.findById(idTipo)).thenReturn(java.util.Optional.of(tipo));
        when(metricaRepository.findByTipo(tipo)).thenReturn(Arrays.asList(metrica1, metrica2));

        // Act
        List<MetricaSistema> resultado = service.obtenerPorTipo(idTipo);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipo().getNombre()).isEqualTo("RENDIMIENTO");
    }

    

}
