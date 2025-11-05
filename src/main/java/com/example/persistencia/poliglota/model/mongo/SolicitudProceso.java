package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "solicitudes_proceso")
public class SolicitudProceso {

    @Id
    private UUID id;

    private Integer usuarioId;

    @DBRef
    private Proceso proceso;

    private LocalDateTime fechaSolicitud;
    private EstadoProceso estado;      // PENDIENTE | EN_CURSO | COMPLETADO
    private String resultado;          // opcional
    private LocalDateTime fechaActualizacion;

    // 🔹 Constructores
    public SolicitudProceso() {
        this.id = UUID.randomUUID();
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = EstadoProceso.PENDIENTE;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public SolicitudProceso(Integer usuarioId, Proceso proceso) {
        this();
        this.usuarioId = usuarioId;
        this.proceso = proceso;
    }

    // 🔹 Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Proceso getProceso() { return proceso; }
    public void setProceso(Proceso proceso) { this.proceso = proceso; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { 
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { 
        this.resultado = resultado; 
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // 🔹 Enum interna
    public enum EstadoProceso {
        PENDIENTE,
        EN_CURSO,
        COMPLETADO
    }
}
