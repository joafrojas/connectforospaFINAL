package connectforospa_usuarios.config;

import connectforospa_usuarios.model.Rol;
import connectforospa_usuarios.model.Usuarios;
import connectforospa_usuarios.repository.RegistroUsuarioRepository;
import connectforospa_usuarios.repository.UsuarioRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class CommandLineConfig {

    @Bean
    public CommandLineRunner precargarUsuarios(RegistroUsuarioRepository usuarioRepo,
                                               UsuarioRolRepository rolRepo) {
        return args -> {
            if (usuarioRepo.count() == 0) {

                // Usuario ADMIN
                Usuarios admin = Usuarios.builder()
                        .nombreUsuario("admin")
                        .email("admin@connectforo.cl")
                        .nombre("Administrador Principal")
                        .estado("ACTIVO")
                        .fechaCreacion(LocalDateTime.now().minusHours(10))
                        .build();
                Rol rolAdmin = new Rol(null, "ROLE_ADMIN", admin.getNombreUsuario(), admin);
                admin.setRoles(List.of(rolAdmin));
                usuarioRepo.save(admin);

                // Usuario MODERADOR
                Usuarios moderador = Usuarios.builder()
                        .nombreUsuario("mod")
                        .email("mod@moderador.connectforo.cl")
                        .nombre("Moderador Uno")
                        .estado("ACTIVO")
                        .fechaCreacion(LocalDateTime.now().minusHours(10))
                        .build();
                Rol rolMod = new Rol(null, "ROLE_MODERADOR", moderador.getNombreUsuario(), moderador);
                moderador.setRoles(List.of(rolMod));
                usuarioRepo.save(moderador);

                // Usuario SOPORTE
                Usuarios soporte = Usuarios.builder()
                        .nombreUsuario("soporte")
                        .email("soporte@soporte.connectforo.cl")
                        .nombre("Soporte Técnico")
                        .estado("ACTIVO")
                        .fechaCreacion(LocalDateTime.now().minusHours(10))
                        .build();
                Rol rolSoporte = new Rol(null, "ROLE_SOPORTE", soporte.getNombreUsuario(), soporte);
                soporte.setRoles(List.of(rolSoporte));
                usuarioRepo.save(soporte);

                // Usuario ANUNCIOS
                Usuarios anuncios = Usuarios.builder()
                        .nombreUsuario("anunciante")
                        .email("ads@anuncios.connectforo.cl")
                        .nombre("Usuario de Anuncios")
                        .estado("ACTIVO")
                        .fechaCreacion(LocalDateTime.now().minusHours(10))
                        .build();
                Rol rolAnuncio = new Rol(null, "ROLE_ANUNCIOS", anuncios.getNombreUsuario(), anuncios);
                anuncios.setRoles(List.of(rolAnuncio));
                usuarioRepo.save(anuncios);

                // Usuario COMÚN
                Usuarios user = Usuarios.builder()
                        .nombreUsuario("usuario1")
                        .email("usuario1@foro.cl")
                        .nombre("Usuario General")
                        .estado("ACTIVO")
                        .fechaCreacion(LocalDateTime.now().minusHours(10))
                        .build();
                Rol rolUser = new Rol(null, "ROLE_USER", user.getNombreUsuario(), user);
                user.setRoles(List.of(rolUser));
                usuarioRepo.save(user);

                System.out.println(" Usuarios precargados con éxito.");
            }
        };
    }
}