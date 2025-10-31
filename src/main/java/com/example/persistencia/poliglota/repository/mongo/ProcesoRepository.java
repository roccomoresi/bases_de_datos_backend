package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


public interface ProcesoRepository extends MongoRepository<Proceso, String> {
    List<Proceso> findByActivoTrue();
    List<Proceso> findByTipoIgnoreCase(String tipo);
}

