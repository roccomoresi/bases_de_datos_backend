package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import java.util.List;

public interface MedicionPorCiudadRepository extends CassandraRepository<MedicionPorCiudad, String> {

    @Query("SELECT * FROM mediciones_por_ciudad WHERE ciudad = ?0 AND pais = ?1")
    List<MedicionPorCiudad> findByCiudadAndPais(String ciudad, String pais);
}
