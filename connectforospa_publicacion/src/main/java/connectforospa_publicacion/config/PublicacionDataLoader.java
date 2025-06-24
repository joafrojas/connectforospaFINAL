package connectforospa_publicacion.config;

import connectforospa_publicacion.model.Publicacion;
import connectforospa_publicacion.model.EstadoPublicacion;
import connectforospa_publicacion.repository.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PublicacionDataLoader implements CommandLineRunner {

    private final PublicacionRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            List<Publicacion> iniciales = List.of(
                Publicacion.builder()
                    .titulo("¿Qué opinan de Java 21?")
                    .contenido("Personalmente me encanta el pattern matching, pero ¿qué piensan ustedes?")
                    .fechaPublicacion(LocalDateTime.now().minusDays(1))
                    .idUsuario(5L) 
                    .idForo(1L)    
                    .idCategoria(1L) 
                    .estadoPublicacion(EstadoPublicacion.PENDIENTE)
                    .build(),

                Publicacion.builder()
                    .titulo("Tips para preparar entrevistas técnicas")
                    .contenido("Estoy armando una guía paso a paso con ejemplos de código, ¡aportan?")
                    .fechaPublicacion(LocalDateTime.now().minusHours(10))
                    .idUsuario(2L) 
                    .idForo(2L)    
                    .idCategoria(2L) 
                    .estadoPublicacion(EstadoPublicacion.PENDIENTE)
                    .build()
            );

            repo.saveAll(iniciales);
            System.out.println("📝 Publicaciones iniciales insertadas.");
        }
    }
}