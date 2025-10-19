package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
import java.time.Instant;
import java.util.UUID;

@Table("controles_por_sensor")
public class ControlFuncionamiento {

    @PrimaryKeyColumn(name = "sensor_id", type = PrimaryKeyType.PARTITIONED)
    private UUID sensorId;

    @PrimaryKeyColumn(name = "fecha_control", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant fechaControl;

    @Column("estado")
    private String estado;

    @Column("observaciones")
    private String observaciones;

    @Column("tecnico")
    private String tecnico;

    public ControlFuncionamiento() {}

    public ControlFuncionamiento(UUID sensorId, Instant fechaControl, String estado, String observaciones, String tecnico) {
        this.sensorId = sensorId;
        this.fechaControl = fechaControl;
        this.estado = estado;
        this.observaciones = observaciones;
        this.tecnico = tecnico;
    }

    // Getters y Setters
    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public Instant getFechaControl() { return fechaControl; }
    public void setFechaControl(Instant fechaControl) { this.fechaControl = fechaControl; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }
}
