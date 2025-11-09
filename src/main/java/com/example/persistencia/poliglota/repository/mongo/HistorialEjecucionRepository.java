package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEjecucionRepository extends MongoRepository<HistorialEjecucion, String> {

    // filtros existentes
    List<HistorialEjecucion> findByUsuarioId(Integer usuarioId);
    List<HistorialEjecucion> findByProcesoId(String procesoId);
    List<HistorialEjecucion> findTop5ByUsuarioIdOrderByFechaFinDesc(Integer usuarioId);
    List<HistorialEjecucion> findByUsuarioIdAndProcesoIdOrderByFechaFinDesc(Integer usuarioId, String procesoId);
    List<HistorialEjecucion> findByFechaInicioBetween(LocalDateTime desde, LocalDateTime hasta);
    List<HistorialEjecucion> findTop10ByOrderByFechaFinDesc();

    // ===== Soporte de VALIDACIÓN =====
    List<HistorialEjecucion> findByValidadoFalseOrderByFechaFinDesc();
    List<HistorialEjecucion> findByValidadoTrueOrderByFechaValidacionDesc();
}