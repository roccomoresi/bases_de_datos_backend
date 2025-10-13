package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDate;
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
    private LocalDate fecha_inicio_emision;




    public Sensor(UUID id, String nombre, String tipo, @Indexed String ciudad, @Indexed String pais, double latitud,
            double longitud, String estado, LocalDate fecha_inicio_emision) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ciudad = ciudad;
        this.pais = pais;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.fecha_inicio_emision = fecha_inicio_emision;
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
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public double getLatitud() {
        return latitud;
    }
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    public double getLongitud() {
        return longitud;
    }
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDate getFechaInicio() {
        return fecha_inicio_emision;
    }
    public void setFechaInicio(LocalDate fecha_inicio_emision) {
        this.fecha_inicio_emision = fecha_inicio_emision;
    }
    public LocalDate getFecha_inicio_emision() {
        return fecha_inicio_emision;
    }
    public void setFecha_inicio_emision(LocalDate fecha_inicio_emision) {
        this.fecha_inicio_emision = fecha_inicio_emision;
    }

    // Getters y Setters
}
