package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "historial_ejecucion")
public class HistorialEjecucion {

    @Id
    private String id;

    private String procesoId;
    private String nombreProceso;
    private Integer usuarioId;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long duracionSegundos; // puede ser null si no se calcula

    private String resultado;

    // ===== Campos de VALIDACIÓN =====
    private Boolean validado = false;            // por defecto NO validado
    private String  validadoPor;                 // quién validó (nombre/usuario)
    private LocalDateTime fechaValidacion;       // cuándo se validó
    private String  observacionesValidacion;     // nota opcional

    // ===== Constructores =====
    public HistorialEjecucion() {
        this.id = UUID.randomUUID().toString();
    }

    public HistorialEjecucion(
            String procesoId,
            String nombreProceso,
            Integer usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String resultado
    ) {
        this.id = UUID.randomUUID().toString();
        this.procesoId = procesoId;
        this.nombreProceso = nombreProceso;
        this.usuarioId = usuarioId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.resultado = resultado;

        if (fechaInicio != null && fechaFin != null && !fechaFin.isBefore(fechaInicio)) {
            this.duracionSegundos = Duration.between(fechaInicio, fechaFin).toSeconds();
        } else {
            this.duracionSegundos = null;
        }
        this.validado = false;
    }

    // ===== Getters / Setters =====
    public String getId() { return id; }

    public String getProcesoId() { return procesoId; }
    public void setProcesoId(String procesoId) { this.procesoId = procesoId; }

    public String getNombreProceso() { return nombreProceso; }
    public void setNombreProceso(String nombreProceso) { this.nombreProceso = nombreProceso; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
        if (this.fechaInicio != null && fechaFin != null && !fechaFin.isBefore(this.fechaInicio)) {
            this.duracionSegundos = Duration.between(this.fechaInicio, fechaFin).toSeconds();
        }
    }

    public Long getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(Long duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    // --- validación
    public Boolean getValidado() { return validado; }
    public void setValidado(Boolean validado) { this.validado = validado; }

    public String getValidadoPor() { return validadoPor; }
    public void setValidadoPor(String validadoPor) { this.validadoPor = validadoPor; }

    public LocalDateTime getFechaValidacion() { return fechaValidacion; }
    public void setFechaValidacion(LocalDateTime fechaValidacion) { this.fechaValidacion = fechaValidacion; }

    public String getObservacionesValidacion() { return observacionesValidacion; }
    public void setObservacionesValidacion(String observacionesValidacion) { this.observacionesValidacion = observacionesValidacion; }

    // ===== Logs =====
    @Override
    public String toString() {
        return String.format(
                "HistorialEjecucion{id=%s, proceso='%s', usuario=%d, estado='%s', duracion=%ds, validado=%s}",
                id, nombreProceso, usuarioId, resumenResultado(),
                duracionSegundos != null ? duracionSegundos : 0,
                Boolean.TRUE.equals(validado)
        );
    }

    private String resumenResultado() {
        if (resultado == null) return "sin resultado";
        if (resultado.toLowerCase().contains("pendiente")) return "pendiente";
        if (resultado.toLowerCase().contains("curso")) return "en curso";
        if (resultado.toLowerCase().contains("éxito") || resultado.toLowerCase().contains("completado"))
            return "completado";
        return "otro";
    }
}