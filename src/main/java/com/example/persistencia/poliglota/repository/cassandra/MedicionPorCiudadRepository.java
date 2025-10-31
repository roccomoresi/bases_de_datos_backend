package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicionPorCiudadRepository extends CassandraRepository<MedicionPorCiudad, String> {
    List<MedicionPorCiudad> findByCiudadAndFechaMedicionBetween(String ciudad, Date desde, Date hasta);
}

