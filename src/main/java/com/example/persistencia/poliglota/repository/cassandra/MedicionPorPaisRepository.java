package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.MedicionPorPais;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicionPorPaisRepository extends CassandraRepository<MedicionPorPais, String> {
    List<MedicionPorPais> findByPaisAndFechaMedicionBetween(String pais, Date desde, Date hasta);
    List<MedicionPorPais> findByPais(String pais);
}

