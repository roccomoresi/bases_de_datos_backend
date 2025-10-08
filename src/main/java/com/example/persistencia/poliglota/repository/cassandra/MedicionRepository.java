package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {

    @Query("SELECT * FROM mediciones WHERE sensor_id = ?0")
    List<Medicion> findBySensor(UUID sensorId);

    @Query("SELECT * FROM mediciones WHERE sensor_id = ?0 AND timestamp >= ?1 AND timestamp <= ?2")
    List<Medicion> findBySensorAndTimestampBetween(UUID sensorId, Instant desde, Instant hasta);
}
