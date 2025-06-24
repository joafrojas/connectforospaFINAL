package com.example.ReportesyModeracionConnectForoSpa.service;

import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;
import com.example.ReportesyModeracionConnectForoSpa.model.EstadoReporte;
import com.example.ReportesyModeracionConnectForoSpa.repository.EstadoReporteRepository;
import com.example.ReportesyModeracionConnectForoSpa.repository.ReporteRepository;
import com.example.ReportesyModeracionConnectForoSpa.webclient.UsuarioClient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final EstadoReporteRepository estadoRepository;
    private final UsuarioClient usuarioClient;

    public Reporte crearReporte(Reporte reporte) {
        validarUsuariosActivos(reporte.getIdUsuarioGenerador(), reporte.getIdUsuarioReportado());
        reporte.setFecha(LocalDateTime.now());
        return reporteRepository.save(reporte);
    }

    public List<Reporte> obtenerTodos() {
        return reporteRepository.findAll();
    }

    public Reporte obtenerPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado con ID: " + id));
    }

    public List<Reporte> obtenerPorGenerador(Long idUsuarioGenerador) {
        return reporteRepository.findByIdUsuarioGenerador(idUsuarioGenerador);
    }

    public List<Reporte> obtenerPorReportado(Long idUsuarioReportado) {
        return reporteRepository.findByIdUsuarioReportado(idUsuarioReportado);
    }

    public Reporte actualizarReporte(Long id, Reporte nuevoReporte) {
        Reporte existente = reporteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado con ID: " + id));

        validarUsuariosActivos(nuevoReporte.getIdUsuarioGenerador(), nuevoReporte.getIdUsuarioReportado());

        existente.setMotivo(nuevoReporte.getMotivo());
        existente.setRespuesta(nuevoReporte.getRespuesta());
        existente.setEstadoReporte(nuevoReporte.getEstadoReporte());

        return reporteRepository.save(existente);
    }

    public Reporte actualizarEstado(Long id, Long idEstadoNuevo) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado con ID: " + id));

        EstadoReporte nuevoEstado = estadoRepository.findById(idEstadoNuevo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado no encontrado con ID: " + idEstadoNuevo));

        reporte.setEstadoReporte(nuevoEstado);
        return reporteRepository.save(reporte);
    }

    public void eliminar(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede eliminar: reporte no encontrado.");
        }
        reporteRepository.deleteById(id);
    }

    private void validarUsuariosActivos(Long idGen, Long idRep) {
        boolean generador = usuarioClient.validarUsuarioActivo(idGen).blockOptional().orElse(false);
        boolean reportado = usuarioClient.validarUsuarioActivo(idRep).blockOptional().orElse(false);

        if (!generador || !reportado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o ambos usuarios no están activos o no existen.");
        }
    }
}