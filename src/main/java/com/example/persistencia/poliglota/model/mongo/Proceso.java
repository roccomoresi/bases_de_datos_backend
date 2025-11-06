package com.example.persistencia.poliglota.model.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

/**
 * Modelo Mongo de Proceso (solo ASCII, sin emojis ni comillas curvas).
 */
@Document(collection = "procesos") // ← si tu colección se llama "proceso", cambialo aquí
public class Proceso {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String tipo;   // informe, alerta, servicio, etc.
    private Double costo;  // usado para facturar
    private boolean activo = true;

    @OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<SolicitudProceso> solicitudes = new ArrayList<>();

    // ----- Constructores -----
    public Proceso() {}

    public Proceso(String id, String nombre, String descripcion, String tipo, Double costo, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.costo = costo;
        this.activo = activo;
    }

    // ----- Getters/Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // ----- toString -----
    @Override
    public String toString() {
        return "Proceso{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", costo=" + costo +
                ", activo=" + activo +
                '}';
    }

    public List<SolicitudProceso> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<SolicitudProceso> solicitudes) {
        this.solicitudes = solicitudes;
    }
}
