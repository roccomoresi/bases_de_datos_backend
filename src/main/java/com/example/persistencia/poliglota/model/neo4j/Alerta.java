package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Node("Alerta")
public class Alerta {

    @Id
    private UUID id = UUID.randomUUID();


    private String tipo; // sensor o climática
    private String nivel;
    
    public Alerta(UUID id, String tipo, String nivel, String descripcion, String estado, LocalDateTime fechaHora,
            SensorNode sensor) {
        this.id = id;
        this.tipo = tipo;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.sensor = sensor;
    }

    private String descripcion;
    private String estado; // activa / resuelta
    private LocalDateTime fechaHora;
    

    @Relationship(type = "GENERADA_POR", direction = Relationship.Direction.OUTGOING)
    private SensorNode sensor;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public SensorNode getSensor() {
        return sensor;
    }

    public void setSensor(SensorNode sensor) {
        this.sensor = sensor;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    // Getters y Setters
}
