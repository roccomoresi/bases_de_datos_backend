package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.Instant;
import java.util.UUID;

@Table("mediciones")
public class Medicion {

    @PrimaryKeyColumn(name = "sensor_id", type = PrimaryKeyType.PARTITIONED)
    private UUID sensorId;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED)
    private Instant timestamp;

    @Column("valor")
    private double valor;

    @Column("unidad")
    private String unidad;

    @Column("tipo")
    private String tipo;

    public Medicion() {}

    public Medicion(UUID sensorId, Instant timestamp, double valor, String unidad, String tipo) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.valor = valor;
        this.unidad = unidad;
        this.tipo = tipo;
    }

    // Getters y Setters
    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
