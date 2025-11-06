package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Sensor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends CassandraRepository<Sensor, UUID> {

    List<Sensor> findByCiudad(String ciudad);
    List<Sensor> findByEstado(String estado);
    List<Sensor> findByTipo(String tipo);
    List<Sensor> findByNombre(String nombre);
}


