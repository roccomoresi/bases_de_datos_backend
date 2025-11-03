package com.example.persistencia.poliglota.repository.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcesoRepository extends MongoRepository<Proceso, String> {

    // Activos: activo = true
    @Query("{ 'activo': true }")
    List<Proceso> findByActivoTrue();

    // Por tipo (case-insensitive). Ej: "informe", "alerta", etc.
    // Usamos regex con opción 'i' para ignorar mayúsculas/minúsculas.
    @Query("{ 'tipo': { $regex: ?0, $options: 'i' } }")
    List<Proceso> findByTipoIgnoreCase(String tipo);
}
