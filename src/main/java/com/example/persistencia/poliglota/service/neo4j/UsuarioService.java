package com.example.persistencia.poliglota.service.neo4j;



import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final Neo4jClient neo4jClient;

    public UsuarioService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<String> getAllUsuarios() {
        return neo4jClient.query("MATCH (u:Usuario) RETURN u.nombre AS nombre")
                .fetch()
                .all()
                .stream()
                .map(m -> (String) m.get("nombre"))
                .toList();
    }
}
