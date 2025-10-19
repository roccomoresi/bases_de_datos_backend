package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "alertas")
public class Alerta {

    @Id
    private UUID id = UUID.randomUUID(); // 👉 ID de alerta

    private String tipo;           // 👉 "sensor" o "climatica"
    private UUID sensorId;         // 👉 si aplica
    private Instant fechaHora;     // 👉 fecha y hora del evento
    private String descripcion;    // 👉 detalle o motivo
    private String estado;         // 👉 "activa" o "resuelta"

    // 🔧 opcional: datos contextuales (te sirven para mostrar en el front)
    private String ciudad;
    private String pais;

    public Alerta() {}

    public Alerta(String tipo, UUID sensorId, String descripcion, String ciudad, String pais) {
        this.tipo = tipo;
        this.sensorId = sensorId;
        this.descripcion = descripcion;
        this.fechaHora = Instant.now();
        this.estado = "activa";
        this.ciudad = ciudad;
        this.pais = pais;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getTipo() { return tipo; }
    public UUID getSensorId() { return sensorId; }
    public Instant getFechaHora() { return fechaHora; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }

    public void setId(UUID id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }
    public void setFechaHora(Instant fechaHora) { this.fechaHora = fechaHora; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setPais(String pais) { this.pais = pais; }
}
