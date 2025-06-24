package com.example.MetricasSistemaConnectForoSpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MetricasSistemaConnectForoSpa.model.MetricaSistema;
import com.example.MetricasSistemaConnectForoSpa.model.TipoMetrica;

public interface MetricaSistemaRepository extends JpaRepository<MetricaSistema, Long> {
    List<MetricaSistema> findByTipo(TipoMetrica tipo);

}
