package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {

    @Query("SELECT * FROM mediciones_por_sensor WHERE sensor_id = ?0")
    List<Medicion> findBySensorId(UUID sensorId);

    @Query("SELECT * FROM mediciones_por_sensor " +
           "WHERE sensor_id = ?0 AND fecha_medicion >= ?1 AND fecha_medicion <= ?2")
    List<Medicion> findBySensorIdAndFechaBetween(UUID sensorId, Instant desde, Instant hasta);

    long countBySensorId(UUID sensorId);
}

