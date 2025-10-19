package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.ControlFuncionamiento;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ControlFuncionamientoRepository extends CassandraRepository<ControlFuncionamiento, UUID> {

    @Query("SELECT * FROM controles_por_sensor WHERE sensor_id = ?0")
    List<ControlFuncionamiento> findBySensorId(UUID sensorId);
}
