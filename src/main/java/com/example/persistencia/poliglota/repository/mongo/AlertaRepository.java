package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface AlertaRepository extends MongoRepository<Alerta, UUID> {
    List<Alerta> findByEstado(String estado);
    List<Alerta> findByCiudadAndPais(String ciudad, String pais);
}
