package com.example.SoporteTecnico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.SoporteTecnico.model.Respuesta;
import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.repository.RespuestaRepository;
import com.example.SoporteTecnico.repository.SoporteTecnicoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RespuestaServiceTest {

    @Mock
    private RespuestaRepository respuestaRepository;

    @Mock
    private SoporteTecnicoRepository soporteTecnicoRepository;

    @InjectMocks
    private RespuestaService respuestaService;

    private SoporteTecnico soporte;

    @BeforeEach
    void setUp() {
        soporte = new SoporteTecnico();
        soporte.setIdSoporte(1L);
        soporte.setEstado("pendiente");
    }

    @Test
    void crearRespuesta_CuandoSoporteExiste_CreaYRetornaRespuesta() {
        // Arrange
        String descripcion = "Hemos resuelto tu problema";
        when(soporteTecnicoRepository.findById(1L)).thenReturn(Optional.of(soporte));
        when(respuestaRepository.save(any(Respuesta.class))).thenAnswer(invoc -> {
            Respuesta r = invoc.getArgument(0);
            r.setIdRespuesta(100L);
            return r;
        });

        // Act
        Respuesta respuesta = respuestaService.crearRespuesta(1L, descripcion);

        // Assert
        assertNotNull(respuesta);
        assertEquals(100L, respuesta.getIdRespuesta());
        assertEquals(descripcion, respuesta.getDescripcion());
        assertEquals(soporte, respuesta.getIdSoporteTecnico());
        assertThat(respuesta.getFechaRespuesta()).isBeforeOrEqualTo(LocalDateTime.now());

        verify(respuestaRepository).save(any(Respuesta.class));
    }

    @Test
    void crearRespuesta_CuandoSoporteNoExiste_LanzaExcepcion() {
        when(soporteTecnicoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            respuestaService.crearRespuesta(99L, "Respuesta que no debería guardarse");
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("No se encontró"));
    }

}
