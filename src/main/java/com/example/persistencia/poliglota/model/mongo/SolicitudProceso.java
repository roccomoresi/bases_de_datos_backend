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
    private UUID usuarioId; // viene del sistema principal (MySQL)
    
    @DBRef
    private Proceso proceso; // referencia al documento de Mongo
    
    private LocalDateTime fechaSolicitud;
    private String estado; // "pendiente", "en_curso", "completado"
    private String resultado; // opcional: resumen del resultado o mensaje final

    public SolicitudProceso() {
        this.id = UUID.randomUUID();
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "pendiente";
    }

    public SolicitudProceso(UUID usuarioId, Proceso proceso) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.proceso = proceso;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "pendiente";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
