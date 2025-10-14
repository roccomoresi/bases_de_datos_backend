package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "alertas")
public class Alerta {

    @Id
    private UUID id = UUID.randomUUID();

    private String tipo;           // "sensor" o "climatica"
    private String descripcion;    // texto descriptivo de la alerta
    private String estado;         // "activa" / "resuelta" / "pendiente"
    private LocalDateTime fechaHora; // momento en que se generó la alerta
    private UUID sensorId;         // referencia al sensor en Cassandra
    private String severidad;      // opcional: "baja", "media", "alta"

    // 🔹 Constructor vacío (requerido por Spring)
    public Alerta() {}

    // 🔹 Constructor con parámetros
    public Alerta(String tipo, String descripcion, String estado, LocalDateTime fechaHora, UUID sensorId, String severidad) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.sensorId = sensorId;
        this.severidad = severidad;
    }

    // 🔹 Getters y Setters
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

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    // 🔹 toString (para debug)
    @Override
    public String toString() {
        return "Alerta{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaHora=" + fechaHora +
                ", sensorId=" + sensorId +
                ", severidad='" + severidad + '\'' +
                '}';
    }
}
