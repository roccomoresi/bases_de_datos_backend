package com.example.persistencia.poliglota.repository.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Alerta;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends Neo4jRepository<Alerta, Long> {
}
