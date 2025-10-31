package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicionRepository extends CassandraRepository<Medicion, UUID> {

    List<Medicion> findBySensorId(UUID sensorId);

    // Buscar por sensor en rango de fechas
    List<Medicion> findBySensorIdAndFechaMedicionBetween(UUID sensorId, Date desde, Date hasta);

    // Buscar por ciudad en rango de fechas
    List<Medicion> findByCiudadAndFechaMedicionBetween(String ciudad, Date desde, Date hasta);

    // Buscar por país en rango de fechas
    List<Medicion> findByPaisAndFechaMedicionBetween(String pais, Date desde, Date hasta);
}

