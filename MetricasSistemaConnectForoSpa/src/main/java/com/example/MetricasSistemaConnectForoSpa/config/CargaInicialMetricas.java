package com.example.MetricasSistemaConnectForoSpa.config;

import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.model.TipoMetrica;
import com.example.MetricasSistemaConnectForoSpa.repository.MetricaSistemaRepository;
import com.example.MetricasSistemaConnectForoSpa.repository.TipoMetricaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class CargaInicialMetricas {

    @Bean
    CommandLineRunner precargarDatos(TipoMetricaRepository tipoRepo, MetricaSistemaRepository metricaRepo) {
        return args -> {

            // Precargar tipos si están vacíos
            if (tipoRepo.count() == 0) {
                tipoRepo.save(new TipoMetrica(null, "RENDIMIENTO"));
                tipoRepo.save(new TipoMetrica(null, "FALLO"));
                tipoRepo.save(new TipoMetrica(null, "LATENCIA"));
                System.out.println("Tipos de métrica precargados");
            }

            // Precargar métricas si no hay ninguna
            if (metricaRepo.count() == 0) {
                TipoMetrica rendimiento = tipoRepo.findById(1L).orElseThrow();
                TipoMetrica fallo = tipoRepo.findById(2L).orElseThrow();
                TipoMetrica latencia = tipoRepo.findById(3L).orElseThrow();

                metricaRepo.save(new MetricaSistema(null, "Carga inicial del servidor", LocalDateTime.now(), rendimiento));
                metricaRepo.save(new MetricaSistema(null, "Error 500 detectado en /usuarios", LocalDateTime.now(), fallo));
                metricaRepo.save(new MetricaSistema(null, "Tiempo de respuesta > 2s", LocalDateTime.now(), latencia));

                System.out.println("Métricas precargadas");
            }
        };
    }
}

