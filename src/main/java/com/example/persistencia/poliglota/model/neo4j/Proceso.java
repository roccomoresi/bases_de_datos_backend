package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@Node("Proceso")
public class Proceso {

    @Id
    private UUID id = UUID.randomUUID();



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
    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Proceso> getDependencias() {
        return dependencias;
    }

    public void setDependencias(List<Proceso> dependencias) {
        this.dependencias = dependencias;
    }
}

