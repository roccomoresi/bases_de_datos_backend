package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface HistorialEjecucionRepository extends MongoRepository<HistorialEjecucion, UUID> {

    // 🔹 Busca por usuario SQL (id entero)
    List<HistorialEjecucion> findByUsuarioId(Integer usuarioId);

    // 🔹 Busca por proceso Mongo (id string)
    List<HistorialEjecucion> findByProcesoId(String procesoId);
}
