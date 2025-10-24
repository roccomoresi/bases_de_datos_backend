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

    @PrimaryKeyColumn(name = "fecha_medicion", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date fechaMedicion; // java.util.Date

    @PrimaryKeyColumn(name = "sensor_id", type = PrimaryKeyType.CLUSTERED)
    private UUID sensorId;

    @Column private Double temperatura;
    @Column private Double humedad;
    @Column private String pais;

    public MedicionPorCiudad() {}

    public MedicionPorCiudad(String ciudad, String pais, Date fechaMedicion, UUID sensorId, Double temperatura, Double humedad) {
        this.ciudad = ciudad;
        this.pais = pais;
        this.fechaMedicion = fechaMedicion;
        this.sensorId = sensorId;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Date getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(Date fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "MedicionPorCiudad [ciudad=" + ciudad + ", fechaMedicion=" + fechaMedicion + ", sensorId=" + sensorId
                + ", temperatura=" + temperatura + ", humedad=" + humedad + ", pais=" + pais + ", getCiudad()="
                + getCiudad() + ", getFechaMedicion()=" + getFechaMedicion() + ", getClass()=" + getClass()
                + ", getSensorId()=" + getSensorId() + ", getTemperatura()=" + getTemperatura() + ", getHumedad()="
                + getHumedad() + ", getPais()=" + getPais() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }

    // getters/setters...
}
