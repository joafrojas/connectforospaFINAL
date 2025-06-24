package com.example.ReportesyModeracionConnectForoSpa.config;

import com.example.ReportesyModeracionConnectForoSpa.model.EstadoReporte;
import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;
import com.example.ReportesyModeracionConnectForoSpa.repository.EstadoReporteRepository;
import com.example.ReportesyModeracionConnectForoSpa.repository.ReporteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class ReportesPrecargados {

    @Bean
    CommandLineRunner cargarDatosIniciales(EstadoReporteRepository estadoRepo, ReporteRepository reporteRepo) {
        return args -> {
            // Precargar estados
            EstadoReporte pendiente = new EstadoReporte(null, "PENDIENTE");
            EstadoReporte resuelto = new EstadoReporte(null, "RESUELTO");

            estadoRepo.save(pendiente);
            estadoRepo.save(resuelto);

            // Precargar reportes
            Reporte r1 = new Reporte(null, "Lenguaje ofensivo", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
            Reporte r2 = new Reporte(null, "Spam en los comentarios", null, LocalDateTime.now(), 1002L, 2002L, pendiente);

            reporteRepo.save(r1);
            reporteRepo.save(r2);
        };
    }
}

