package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface ProcesoRepository extends MongoRepository<Proceso, UUID> {
    List<Proceso> findByActivoTrue();
    List<Proceso> findByTipoIgnoreCase(String tipo);
}
