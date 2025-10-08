package com.example.persistencia.poliglota.repository.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Usuario;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface UsuarioRepository extends Neo4jRepository<Usuario, String> {

    @Query("MATCH (u:Usuario)-[:COLABORA_CON]->(colab) RETURN u, collect(colab)")
    List<Usuario> findAllWithColaboradores();

    @Query("MATCH (u:Usuario {nombre:$nombre})-[:EJECUTA]->(p:Proceso) RETURN u, collect(p)")
    Usuario findByNombreWithProcesos(String nombre);

    @Query("MATCH (u:Usuario) RETURN u")
List<Usuario> findAllUsuarios();

}
