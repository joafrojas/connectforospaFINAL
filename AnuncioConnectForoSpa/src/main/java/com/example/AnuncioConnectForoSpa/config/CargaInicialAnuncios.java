package com.example.AnuncioConnectForoSpa.config;

import com.example.AnuncioConnectForoSpa.model.Anuncio;
import com.example.AnuncioConnectForoSpa.model.Estado;
import com.example.AnuncioConnectForoSpa.repository.AnuncioRepository;
import com.example.AnuncioConnectForoSpa.repository.EstadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class CargaInicialAnuncios {

    @Bean
    CommandLineRunner precargarAnuncios(AnuncioRepository anuncioRepo, EstadoRepository estadoRepo) {
        return args -> {
            if (estadoRepo.count() == 0) {
                estadoRepo.save(new Estado(1L, "ACTIVO"));
                estadoRepo.save(new Estado(2L, "INACTIVO"));
                System.out.println("Estados precargados");
            }

            if (anuncioRepo.count() == 0) {
                anuncioRepo.save(Anuncio.builder()
                        .titulo("Suspensión de clases por elecciones")
                        .foto("suspension.jpg")
                        .fechaPublicacion(LocalDateTime.of(2025, 6, 10, 9, 0))
                        .prioridad("ALTA")
                        .usuarioId(1L) // Cambiado aquí
                        .estado(estadoRepo.findById(1L).orElse(null))
                        .build());

                anuncioRepo.save(Anuncio.builder()
                        .titulo("Proceso de evaluación docente")
                        .foto("evaluacion.jpg")
                        .fechaPublicacion(LocalDateTime.of(2025, 6, 12, 14, 30))
                        .prioridad("MEDIA")
                        .usuarioId(2L) // Cambiado aquí
                        .estado(estadoRepo.findById(1L).orElse(null))
                        .build());

                anuncioRepo.save(Anuncio.builder()
                        .titulo("Cierre temporal de biblioteca")
                        .foto("biblioteca.jpg")
                        .fechaPublicacion(LocalDateTime.of(2025, 6, 14, 11, 15))
                        .prioridad("BAJA")
                        .usuarioId(3L) // Cambiado aquí
                        .estado(estadoRepo.findById(2L).orElse(null))
                        .build());

                System.out.println("Anuncios institucionales precargados");
            }
        };
    }
}
