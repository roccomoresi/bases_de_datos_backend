package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Table("mediciones_por_ciudad")
public class MedicionPorCiudad {

    @PrimaryKeyColumn(name = "ciudad", type = PrimaryKeyType.PARTITIONED)
    private String ciudad;

    @PrimaryKeyColumn(name = "pais", type = PrimaryKeyType.PARTITIONED)
    private String pais;

    @PrimaryKeyColumn(name = "fecha_medicion", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date fechaMedicion;

    @Column("sensor_id")
    private UUID sensorId;

    @Column("temperatura")
    private double temperatura;

    @Column("humedad")
    private double humedad;

    public MedicionPorCiudad() {}

    public MedicionPorCiudad(String ciudad, String pais, Date fechaMedicion,
                         UUID sensorId, double temperatura, double humedad) {
    this.ciudad = ciudad;
    this.pais = pais;
    this.fechaMedicion = fechaMedicion;
    this.sensorId = sensorId;
    this.temperatura = temperatura;
    this.humedad = humedad;
}


    // Getters y setters
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Date getFechaMedicion() { return fechaMedicion; }
    public void setFechaMedicion(Date fechaMedicion) { this.fechaMedicion = fechaMedicion; }

    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    public double getHumedad() { return humedad; }
    public void setHumedad(double humedad) { this.humedad = humedad; }
}
