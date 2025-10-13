package com.example.persistencia.poliglota.repository.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Ciudad;

import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepository extends Neo4jRepository<Ciudad, UUID> {
}
