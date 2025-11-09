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
    private Long duracionSegundos; // puede ser null si no se calcula aún

    private String resultado;

    /* ───────────────────────────────
       🔹 Constructores
    ─────────────────────────────── */
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
    }

    /* ───────────────────────────────
       🔹 Getters y Setters
    ─────────────────────────────── */
    public String getId() { return id; } // ← corregido: devuelve String

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
        } else {
            this.duracionSegundos = null; // ← asegura consistencia
        }
    }

    public Long getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(Long duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    /* ───────────────────────────────
       🔹 Para depuración en logs
    ─────────────────────────────── */
    @Override
    public String toString() {
        return String.format(
                "HistorialEjecucion{id=%s, proceso='%s', usuario=%d, estado='%s', duracion=%ds}",
                id, nombreProceso, usuarioId, resumenResultado(), duracionSegundos != null ? duracionSegundos : 0
        );
    }

    private String resumenResultado() {
        if (resultado == null) return "sin resultado";
        String r = resultado.toLowerCase();
        if (r.contains("pendiente")) return "pendiente";
        if (r.contains("curso")) return "en curso";
        if (r.contains("éxito") || r.contains("exito") || r.contains("completado")) return "completado";
        return "otro";
    }
}