package connectforospa_gestiondecategorias.config;

import connectforospa_gestiondecategorias.model.Categorias;
import connectforospa_gestiondecategorias.repository.GestionDeCategoriasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final GestionDeCategoriasRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            List<Categorias> iniciales = List.of(
                Categorias.builder()
                        .nombre("Tecnología")
                        .descripcion("Discusión sobre avances y gadgets")
                        .build(),
                Categorias.builder()
                        .nombre("Programación")
                        .descripcion("Lenguajes, frameworks y buenas prácticas")
                        .build(),
                Categorias.builder()
                        .nombre("Juegos")
                        .descripcion("Videojuegos, consolas y comunidad gamer")
                        .build()
            );
            repo.saveAll(iniciales);
            System.out.println(" Categorías iniciales precargadas");
        }
    }
}