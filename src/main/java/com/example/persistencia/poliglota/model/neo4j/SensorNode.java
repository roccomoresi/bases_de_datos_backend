package com.example.persistencia.poliglota.model.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.*;

@Node("Sensor")
public class SensorNode {

    @Id
    private UUID id = UUID.randomUUID();;
    private String nombre;
    private String ciudad;

    @Relationship(type = "UBICADO_EN", direction = Relationship.Direction.OUTGOING)
    private Ciudad ciudadRelacion;

    public SensorNode(UUID id, String nombre, String ciudad, Ciudad ciudadRelacion) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.ciudadRelacion = ciudadRelacion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Ciudad getCiudadRelacion() {
        return ciudadRelacion;
    }

    public void setCiudadRelacion(Ciudad ciudadRelacion) {
        this.ciudadRelacion = ciudadRelacion;
    }

    // Getters y Setters
}
