package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistorialEjecucionRepository extends MongoRepository<HistorialEjecucion, UUID> {

    // 🔹 Buscar por usuario (id SQL)
    List<HistorialEjecucion> findByUsuarioId(Integer usuarioId);

    // 🔹 Buscar por proceso (id Mongo)
    List<HistorialEjecucion> findByProcesoId(String procesoId);

    // 🔹 Historial más reciente por usuario
    List<HistorialEjecucion> findTop5ByUsuarioIdOrderByFechaFinDesc(Integer usuarioId);

    // 🔹 Historial por usuario y proceso ordenado por fecha
    List<HistorialEjecucion> findByUsuarioIdAndProcesoIdOrderByFechaFinDesc(Integer usuarioId, String procesoId);

    // 🔹 Buscar entre fechas (útil para reportes)
    List<HistorialEjecucion> findByFechaInicioBetween(LocalDateTime desde, LocalDateTime hasta);

    // 🔹 Buscar últimos registros globales
    List<HistorialEjecucion> findTop10ByOrderByFechaFinDesc();
}
