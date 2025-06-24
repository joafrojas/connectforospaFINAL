package com.example.SoporteTecnico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.example.SoporteTecnico.model.SoporteTecnico;
import com.example.SoporteTecnico.repository.SoporteTecnicoRepository;
import com.example.SoporteTecnico.webClient.UsuariosClient;
import com.example.SoporteTecnico.webClient.dto.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class SoporteTecnicoServiceTest {

    @Mock
    private SoporteTecnicoRepository soporteTecnicoRepository;

    @Mock
    private UsuariosClient usuariosClient;

    @InjectMocks
    private SoporteTecnicoService soporteTecnicoService;

    private SoporteTecnico soporte;

    @BeforeEach
    void setUp() {
        soporte = new SoporteTecnico();
        soporte.setIdUsuario(10L);
        soporte.setDescripcion("No puedo comentar en el foro.");
        soporte.setEstado("pendiente");
    }

    

    @Test
    void guardarSoporte_CuandoUsuarioNoExiste_LanzaExcepcion() {
        when(usuariosClient.obtenerUsuarioPorId(10L)).thenReturn(Mono.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            soporteTecnicoService.save(soporte);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("el usuario con ID 10 no existe"));
    }


    @Test
    void obtenerSoportesPorUsuario_DevuelveLista() {
        when(soporteTecnicoRepository.findByIdUsuario(10L)).thenReturn(List.of(soporte));

        List<SoporteTecnico> lista = soporteTecnicoService.obtenerSoportePorUsuario(10L);

        assertThat(lista).isNotEmpty();
        assertEquals(1, lista.size());
        verify(soporteTecnicoRepository).findByIdUsuario(10L);
    }

    @Test
    void obtenerSoportePorId_CuandoExiste_RetornaSoporte() {
        when(soporteTecnicoRepository.findById(1L)).thenReturn(Optional.of(soporte));

        SoporteTecnico resultado = soporteTecnicoService.obtenerSoportePorId(1L);

        assertNotNull(resultado);
        assertEquals("pendiente", resultado.getEstado());
    }

    @Test
    void eliminarSoporte_CuandoIdValido_NoLanzaExcepcion() {
        Long idValido = 1L;

        when(soporteTecnicoRepository.existsById(idValido)).thenReturn(true);
        doNothing().when(soporteTecnicoRepository).deleteById(idValido);

        assertDoesNotThrow(() -> soporteTecnicoService.delete(idValido));

        verify(soporteTecnicoRepository, times(1)).deleteById(idValido);
    }

}