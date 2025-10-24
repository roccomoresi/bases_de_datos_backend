package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {

    List<Medicion> findBySensorId(UUID sensorId);

    List<Medicion> findBySensorIdAndFechaMedicionBetween(UUID sensorId, Date inicio, Date fin);

}

