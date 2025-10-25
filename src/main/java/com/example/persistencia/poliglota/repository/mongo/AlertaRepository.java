package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.UUID;

public interface AlertaRepository extends MongoRepository<Alerta, UUID> {

    List<Alerta> findByEstado(String estado);
    List<Alerta> findByCiudadAndPais(String ciudad, String pais);

    // 🎯 Query dinámica: filtra por cualquier combinación de campos
    @Query("{ '$and': [ "
         + "  { $or: [ { 'tipo': ?0 }, { ?0: null } ] },"
         + "  { $or: [ { 'severidad': ?1 }, { ?1: null } ] },"
         + "  { $or: [ { 'ciudad': ?2 }, { ?2: null } ] },"
         + "  { $or: [ { 'pais': ?3 }, { ?3: null } ] }"
         + "] }")
    List<Alerta> filtrarAlertas(String tipo, String severidad, String ciudad, String pais);
}
