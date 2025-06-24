package com.example.InteraccionesSociales2;

import org.springframework.stereotype.Component;

import com.example.InteraccionesSociales2.model.TipoInteraccion;
import com.example.InteraccionesSociales2.repository.TipoInteraccionRepository;

import jakarta.annotation.PostConstruct;
@Component
public class DatosPrecargados {
    private final TipoInteraccionRepository tipoInteraccionesRepository;

    public DatosPrecargados(TipoInteraccionRepository tipoInteraccionesRepository) {
    this.tipoInteraccionesRepository = tipoInteraccionesRepository;
    }


    @PostConstruct
    public void init() {
        if (tipoInteraccionesRepository.count() == 0) {
            tipoInteraccionesRepository.save(new TipoInteraccion(1L, "like"));
            tipoInteraccionesRepository.save(new TipoInteraccion(2L, "compartir"));
            tipoInteraccionesRepository.save(new TipoInteraccion(3L, "me encanta"));
            System.out.println("✔ Tipos de interacción precargados correctamente.");
        }
    }

}
