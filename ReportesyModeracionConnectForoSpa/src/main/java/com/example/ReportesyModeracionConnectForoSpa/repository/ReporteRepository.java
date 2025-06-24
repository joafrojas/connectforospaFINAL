package com.example.ReportesyModeracionConnectForoSpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.ReportesyModeracionConnectForoSpa.model.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByIdUsuarioGenerador(Long idUsuarioGenerador);
    List<Reporte> findByIdUsuarioReportado(Long idUsuarioReportado);
}

