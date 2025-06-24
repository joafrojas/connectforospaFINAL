package connectforospa_foro.config;

import connectforospa_foro.model.Foro;
import connectforospa_foro.repository.ForoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ForoDataLoader implements CommandLineRunner {

    private final ForoRepository foroRepo;

    @Override
    public void run(String... args) {
        if (foroRepo.count() == 0) {
            List<Foro> forosIniciales = List.of(
                Foro.builder().nombreForo("Tecnología y Gadgets").idUsuario(1L).build(),
                Foro.builder().nombreForo("Proyectos de Software").idUsuario(2L).build(),
                Foro.builder().nombreForo("Comunidad de Estudiantes").idUsuario(1L).build()
            );

            foroRepo.saveAll(forosIniciales);
            System.out.println(" Foros precargados en la base de datos");
        }
    }
}