package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.mapping.*;
import java.time.Instant;
import java.util.UUID;

@Table("sensores")
public class Sensor {

    @PrimaryKey
    private UUID id;

    private String nombre;
    private String tipo; // temperatura / humedad

    @Indexed
    private String ciudad;

    @Indexed
    private String pais;

    private double latitud;
    private double longitud;
    private String estado; // activo / inactivo / falla

    @Column("fecha_inicio_emision")
    private Instant fechaInicioEmision;

    public Sensor() {}

    public Sensor(UUID id, String nombre, String tipo, String ciudad, String pais,
                  double latitud, double longitud, String estado, Instant fechaInicioEmision) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ciudad = ciudad;
        this.pais = pais;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.fechaInicioEmision = fechaInicioEmision;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Instant getFechaInicioEmision() { return fechaInicioEmision; }
    public void setFechaInicioEmision(Instant fechaInicioEmision) { this.fechaInicioEmision = fechaInicioEmision; }
}
