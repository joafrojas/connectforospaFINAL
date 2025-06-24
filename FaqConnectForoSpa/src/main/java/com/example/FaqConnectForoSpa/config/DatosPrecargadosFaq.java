package com.example.FaqConnectForoSpa.config;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import com.example.FaqConnectForoSpa.repository.PreguntaFrecuenteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosPrecargadosFaq {

    @Bean
    CommandLineRunner cargarPreguntasIniciales(PreguntaFrecuenteRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new PreguntaFrecuente(null, "¿Cómo resetear mi contraseña?", "Para resetear la contraseña, haz click en 'Olvidé mi contraseña' en la pantalla de login."));
                repo.save(new PreguntaFrecuente(null, "¿Cómo crear una cuenta?", "Para crear una cuenta, presiona el botón 'Registrarse' y completa el formulario."));
                repo.save(new PreguntaFrecuente(null, "¿Cómo contacto soporte?", "Puedes contactar soporte a través del correo soporte@connectforo.com."));
            }
        };
    }
}
