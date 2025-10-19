package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface HistorialEjecucionRepository extends MongoRepository<HistorialEjecucion, UUID> {
    List<HistorialEjecucion> findByUsuarioId(UUID usuarioId);
    List<HistorialEjecucion> findByProcesoId(UUID procesoId);
}
