package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "historial_ejecuciones")
public class HistorialEjecucion {

    @Id
    private UUID id;
    private String procesoId;
    private String nombreProceso;
    private Integer usuarioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private long duracionSegundos;
    private String resultado;

     public HistorialEjecucion() {
        this.id = UUID.randomUUID();
    }

    public HistorialEjecucion(String procesoId, String nombreProceso, Integer usuarioId,
                              LocalDateTime fechaInicio, LocalDateTime fechaFin, String resultado) {
        this.id = UUID.randomUUID();
        this.procesoId = procesoId;
        this.nombreProceso = nombreProceso;
        this.usuarioId = usuarioId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracionSegundos =
                java.time.Duration.between(fechaInicio, fechaFin).getSeconds();
        this.resultado = resultado;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getProcesoId() { return procesoId; }
    public void setProcesoId(String procesoId) { this.procesoId = procesoId; }
    public String getNombreProceso() { return nombreProceso; }
    public void setNombreProceso(String nombreProceso) { this.nombreProceso = nombreProceso; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public long getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(long duracionSegundos) { this.duracionSegundos = duracionSegundos; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
