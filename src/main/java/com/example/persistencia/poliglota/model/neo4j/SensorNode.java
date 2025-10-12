package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

@Node("Sensor")
public class SensorNode {

    @Id
    private String id;
    private String nombre;
    private String ciudad;

    @Relationship(type = "UBICADO_EN", direction = Relationship.Direction.OUTGOING)
    private Ciudad ciudadRelacion;

    // Getters y Setters
}
