package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

@Node("Ciudad")
public class Ciudad {

    @Id
    private String nombre;
    private String pais;

    // Getters y Setters
}

