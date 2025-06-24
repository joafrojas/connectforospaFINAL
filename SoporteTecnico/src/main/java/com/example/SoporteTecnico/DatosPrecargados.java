package com.example.SoporteTecnico;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.SoporteTecnico.model.MotivoSoporte;
import com.example.SoporteTecnico.model.Respuesta;
import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.repository.MotivoSoporteRepository;
import com.example.SoporteTecnico.repository.RespuestaRepository;
import com.example.SoporteTecnico.repository.SoporteTecnicoRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DatosPrecargados {

    private final MotivoSoporteRepository motivoSoporteRepository;
    private final SoporteTecnicoRepository soporteTecnicoRepository;
    private final RespuestaRepository respuestaRepository;

    public DatosPrecargados(MotivoSoporteRepository motivoSoporteRepository,
                            SoporteTecnicoRepository soporteTecnicoRepository,
                            RespuestaRepository respuestaRepository) {
        this.motivoSoporteRepository = motivoSoporteRepository;
        this.soporteTecnicoRepository = soporteTecnicoRepository;
        this.respuestaRepository = respuestaRepository;
    }

    @PostConstruct
    public void init() {
        if (motivoSoporteRepository.count() == 0) {
            motivoSoporteRepository.save(new MotivoSoporte(1L, "Problemas En foros"));
            motivoSoporteRepository.save(new MotivoSoporte(2L, "Reportes de spam"));
            motivoSoporteRepository.save(new MotivoSoporte(3L, "Errores de carga o visualización de contenido"));
            motivoSoporteRepository.save(new MotivoSoporte(4L, "Problemas en perfil"));
            System.out.println("✔ Motivos precargados correctamente.");
        }

        if (soporteTecnicoRepository.count() == 0) {
            MotivoSoporte motivo = motivoSoporteRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Motivo no encontrado"));

            SoporteTecnico soporte = new SoporteTecnico();
            soporte.setIdUsuario(123L); // Usuario ficticio
            soporte.setMotivo(motivo);
            soporte.setDescripcion("No puedo crear una publicación en el foro.");
            soporte.setEstado("pendiente");

            soporte = soporteTecnicoRepository.save(soporte);

            Respuesta respuesta = new Respuesta();
            respuesta.setIdSoporteTecnico(soporte);
            respuesta.setDescripcion("Revisamos tu cuenta y ya deberías poder publicar con normalidad.");
            respuesta.setFechaRespuesta(LocalDateTime.now());

            respuestaRepository.save(respuesta);

            System.out.println("✔ Soporte y respuesta precargados correctamente.");
        }
    }
}



