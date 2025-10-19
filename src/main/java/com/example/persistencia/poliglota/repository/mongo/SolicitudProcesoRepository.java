package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface SolicitudProcesoRepository extends MongoRepository<SolicitudProceso, UUID> {
    List<SolicitudProceso> findByUsuarioId(UUID usuarioId);
    List<SolicitudProceso> findByEstadoIgnoreCase(String estado);
}
