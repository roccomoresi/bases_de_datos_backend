package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.*;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicionPorSensorRepository extends CassandraRepository<MedicionPorSensor, UUID> {

    List<MedicionPorSensor> findBySensorIdAndFechaMedicionBetween(UUID sensorId, Date desde, Date hasta);

     List<MedicionPorSensor> findBySensorId(UUID sensorId);

}




