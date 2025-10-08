package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("Proceso")
public class Proceso {

    @Id
    private String id = java.util.UUID.randomUUID().toString();


    private String nombre;
    private String tipo;
    private String estado;

    @Relationship(type = "DEPENDE_DE")
    private List<Proceso> dependencias;

    public Proceso() {}

    public Proceso(String nombre, String tipo, String estado) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
}

