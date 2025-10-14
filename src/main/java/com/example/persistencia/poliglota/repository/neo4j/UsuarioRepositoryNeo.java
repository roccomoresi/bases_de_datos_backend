package com.example.persistencia.poliglota.repository.neo4j;

import com.example.persistencia.poliglota.model.neo4j.UsuarioNeo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.UUID;

public interface UsuarioRepositoryNeo extends Neo4jRepository<UsuarioNeo, UUID> {

    @Query("MATCH (u:Usuario)-[:COLABORA_CON]->(colab) RETURN u, collect(colab)")
    List<UsuarioNeo> findAllWithColaboradores();

    @Query("MATCH (u:Usuario {nombre:$nombre})-[:EJECUTA]->(p:Proceso) RETURN u, collect(p)")
    UsuarioNeo findByNombreWithProcesos(String nombre);

    @Query("MATCH (u:Usuario) RETURN u")
List<UsuarioNeo> findAllUsuarios();

}
