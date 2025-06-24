package com.example.ReportesyModeracionConnectForoSpa.service;

import com.example.ReportesyModeracionConnectForoSpa.model.EstadoReporte;
import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;
import com.example.ReportesyModeracionConnectForoSpa.repository.EstadoReporteRepository;
import com.example.ReportesyModeracionConnectForoSpa.repository.ReporteRepository;
import com.example.ReportesyModeracionConnectForoSpa.webclient.UsuarioClient;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private EstadoReporteRepository estadoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ReporteService reporteService;

    EstadoReporte pendiente = new EstadoReporte(1L, "PENDIENTE");
    EstadoReporte resuelto = new EstadoReporte(2L, "RESUELTO");

    @Test
    void crearReporte_debeGuardarYRetornarReporte() {
        Reporte nuevo = new Reporte(null, "Motivo test", null, null, 1001L, 2001L, pendiente);
        Reporte guardado = new Reporte(1L, "Motivo test", null, LocalDateTime.now(), 1001L, 2001L, pendiente);

        // Mock validación usuarios activos
        when(usuarioClient.validarUsuarioActivo(1001L)).thenReturn(Mono.just(true));
        when(usuarioClient.validarUsuarioActivo(2001L)).thenReturn(Mono.just(true));

        when(reporteRepository.save(any(Reporte.class))).thenReturn(guardado);

        Reporte resultado = reporteService.crearReporte(nuevo);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getMotivo()).isEqualTo("Motivo test");
    }

    @Test
    void crearReporte_lanzaErrorSiUsuarioNoActivo() {
        Reporte nuevo = new Reporte(null, "Motivo test", null, null, 1001L, 2001L, pendiente);

        when(usuarioClient.validarUsuarioActivo(1001L)).thenReturn(Mono.just(false));
        when(usuarioClient.validarUsuarioActivo(2001L)).thenReturn(Mono.just(true));

        try {
            reporteService.crearReporte(nuevo);
            assert false : "Se esperaba excepción por usuario no activo";
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("no están activos");
        }
    }

    @Test
    void obtenerTodos_debeRetornarLista() {
        Reporte r1 = new Reporte(1L, "Motivo1", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        Reporte r2 = new Reporte(2L, "Motivo2", null, LocalDateTime.now(), 1002L, 2002L, pendiente);

        when(reporteRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Reporte> resultado = reporteService.obtenerTodos();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void obtenerPorId_debeRetornarReporteSiExiste() {
        Reporte r = new Reporte(1L, "Motivo test", null, LocalDateTime.now(), 1001L, 2001L, pendiente);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));

        Reporte resultado = reporteService.obtenerPorId(1L);

        assertThat(resultado.getMotivo()).isEqualTo("Motivo test");
    }

    @Test
    void obtenerPorGenerador_debeRetornarListaFiltrada() {
        Reporte r = new Reporte(1L, "Motivo G", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteRepository.findByIdUsuarioGenerador(1001L)).thenReturn(List.of(r));

        List<Reporte> resultado = reporteService.obtenerPorGenerador(1001L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuarioGenerador()).isEqualTo(1001L);
    }

    @Test
    void obtenerPorReportado_debeRetornarListaFiltrada() {
        Reporte r = new Reporte(1L, "Motivo R", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        when(reporteRepository.findByIdUsuarioReportado(2001L)).thenReturn(List.of(r));

        List<Reporte> resultado = reporteService.obtenerPorReportado(2001L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuarioReportado()).isEqualTo(2001L);
    }

    @Test
    void actualizarReporte_debeActualizarMotivoYRespuestaYEstado() {
        Reporte original = new Reporte(1L, "Viejo motivo", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        Reporte cambios = new Reporte(null, "Nuevo motivo", "Ya fue revisado", null, 1001L, 2001L, resuelto);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(usuarioClient.validarUsuarioActivo(1001L)).thenReturn(Mono.just(true));
        when(usuarioClient.validarUsuarioActivo(2001L)).thenReturn(Mono.just(true));
        when(reporteRepository.save(original)).thenReturn(original);

        Reporte resultado = reporteService.actualizarReporte(1L, cambios);

        assertThat(resultado.getMotivo()).isEqualTo("Nuevo motivo");
        assertThat(resultado.getRespuesta()).isEqualTo("Ya fue revisado");
        assertThat(resultado.getEstadoReporte().getNombre()).isEqualTo("RESUELTO");
    }

    @Test
    void actualizarReporte_lanzaErrorSiUsuarioNoActivo() {
        Reporte original = new Reporte(1L, "Viejo motivo", null, LocalDateTime.now(), 1001L, 2001L, pendiente);
        Reporte cambios = new Reporte(null, "Nuevo motivo", "Ya fue revisado", null, 1001L, 2001L, resuelto);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(usuarioClient.validarUsuarioActivo(1001L)).thenReturn(Mono.just(false));
        when(usuarioClient.validarUsuarioActivo(2001L)).thenReturn(Mono.just(true));

        try {
            reporteService.actualizarReporte(1L, cambios);
            assert false : "Se esperaba excepción por usuario no activo";
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("no están activos");
        }
    }

    @Test
    void actualizarEstado_debeActualizarSoloEstado() {
        Reporte existente = new Reporte(1L, "Spam", null, LocalDateTime.now(), 1001L, 2001L, pendiente);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(estadoRepository.findById(2L)).thenReturn(Optional.of(resuelto));
        when(reporteRepository.save(existente)).thenReturn(existente);

        Reporte resultado = reporteService.actualizarEstado(1L, 2L);

        assertThat(resultado.getEstadoReporte().getNombre()).isEqualTo("RESUELTO");
    }

    @Test
    void eliminar_debeLlamarADelete() {
        // Mock que el reporte SÍ existe
        when(reporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reporteRepository).deleteById(1L);

        reporteService.eliminar(1L);

        verify(reporteRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        // Mock que el reporte NO existe
        when(reporteRepository.existsById(2L)).thenReturn(false);

        try {
            reporteService.eliminar(2L);
            assert false : "Se esperaba una ResponseStatusException";
        } catch (ResponseStatusException e) {
            assertThat(e.getStatusCode().value()).isEqualTo(404);
            assertThat(e.getReason()).contains("No se puede eliminar: reporte no encontrado.");
        }

    }
}
