package com.example.persistencia.poliglota.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Document(collection = "alertas")
public class Alerta {

    @Id
    private UUID id;
    private String tipo;
    private UUID sensorId;
    private String ciudad;
    private String pais;
    private Instant fecha;
    private String descripcion;
    private String estado;        // activa / resuelta
    private String severidad;     // baja / moderada / critica
    private String color;         // código HEX
    private String icono;         // emoji o ícono
    private String fuente;        // origen de la alerta (manual, cassandra, etc.)
    private Map<String, Object> detalles = new HashMap<>();

    public Alerta() {}

    public Alerta(UUID id, String tipo, UUID sensorId, String ciudad, String pais,
                  Instant fecha, String descripcion, String estado, String severidad,
                  String color, String icono, String fuente, Map<String, Object> detalles) {
        this.id = id;
        this.tipo = tipo;
        this.sensorId = sensorId;
        this.ciudad = ciudad;
        this.pais = pais;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
        this.severidad = severidad;
        this.color = color;
        this.icono = icono;
        this.fuente = fuente;
        this.detalles = detalles != null ? detalles : new HashMap<>();
    }

    // 🔹 Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getSeveridad() { return severidad; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public Map<String, Object> getDetalles() { return detalles; }
    public void setDetalles(Map<String, Object> detalles) { this.detalles = detalles; }
}
