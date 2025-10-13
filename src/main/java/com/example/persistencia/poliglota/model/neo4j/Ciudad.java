package com.example.persistencia.poliglota.model.neo4j;

import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.*;

@Node("Ciudad")
public class Ciudad {

    @Id
    private UUID id = UUID.randomUUID();



    private String nombre;
    private String pais;

    @Relationship(type = "TIENE_SENSOR", direction = Relationship.Direction.INCOMING)
    private List<SensorNode> sensores;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    // Getters y Setters
}

