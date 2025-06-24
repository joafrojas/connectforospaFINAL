package com.example.FaqConnectForoSpa.service;

import com.example.FaqConnectForoSpa.model.PreguntaFrecuente;
import com.example.FaqConnectForoSpa.repository.PreguntaFrecuenteRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreguntaFrecuenteServiceTest {

    @Mock
    private PreguntaFrecuenteRepository repository;

    @InjectMocks
    private PreguntaFrecuenteService service;

    @Test
    void getAll_debeRetornarLista() {
        List<PreguntaFrecuente> mockList = Arrays.asList(
            new PreguntaFrecuente(1L, "¿Cómo resetear contraseña?", "Sigue estos pasos...")
        );

        when(repository.findAll()).thenReturn(mockList);

        List<PreguntaFrecuente> resultado = service.getAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPregunta()).contains("contraseña");
    }

    @Test
    void getById_debeRetornarPreguntaSiExiste() {
        PreguntaFrecuente pregunta = new PreguntaFrecuente(1L, "¿Cómo crear cuenta?", "Simplemente...");
        when(repository.findById(1L)).thenReturn(Optional.of(pregunta));

        PreguntaFrecuente resultado = service.getById(1L);

        assertThat(resultado.getPregunta()).isEqualTo("¿Cómo crear cuenta?");
    }

    @Test
    void save_debeGuardarYPasar() {
        PreguntaFrecuente nueva = new PreguntaFrecuente(null, "¿Cómo cambiar email?", "Aquí...");
        PreguntaFrecuente guardada = new PreguntaFrecuente(1L, "¿Cómo cambiar email?", "Aquí...");

        when(repository.save(nueva)).thenReturn(guardada);

        PreguntaFrecuente resultado = service.save(nueva);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPregunta()).isEqualTo("¿Cómo cambiar email?");
    }

    @Test
    void update_debeModificarYGuardar() {
        PreguntaFrecuente existente = new PreguntaFrecuente(1L, "Pregunta vieja", "Respuesta vieja");
        PreguntaFrecuente cambios = new PreguntaFrecuente(null, "Pregunta nueva", "Respuesta nueva");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        PreguntaFrecuente resultado = service.update(1L, cambios);

        assertThat(resultado.getPregunta()).isEqualTo("Pregunta nueva");
        assertThat(resultado.getRespuesta()).isEqualTo("Respuesta nueva");
    }

    @Test
    void delete_debeLlamarAlRepositorio() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void filtrarPorPalabraClave_debeRetornarListaFiltrada() {
        String palabra = "contraseña";
        PreguntaFrecuente p1 = new PreguntaFrecuente(1L, "¿Cómo resetear contraseña?", "Instrucciones...");
        PreguntaFrecuente p2 = new PreguntaFrecuente(2L, "¿Qué hacer si olvido contraseña?", "Pasos...");

        when(repository.findByPreguntaContainingIgnoreCase(palabra)).thenReturn(Arrays.asList(p1, p2));

        List<PreguntaFrecuente> resultado = service.filtrarPorPalabraClave(palabra);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getPregunta()).contains("contraseña");
    }
}
