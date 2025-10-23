package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("mediciones_por_sensor")
public class Medicion {

    @PrimaryKeyColumn(name = "sensor_id", type = PrimaryKeyType.PARTITIONED)
    private UUID sensorId;

    @PrimaryKeyColumn(name = "fecha_medicion", type = PrimaryKeyType.CLUSTERED)
    private Date fechaMedicion;

    private String ciudad;
    private String pais;
    private double temperatura;
    private double humedad;

    // 🔹 Constructor vacío (obligatorio para Cassandra)
    public Medicion() {}

    // 🔹 Constructor con todos los campos (para uso en servicios)
    public Medicion(UUID sensorId, Date fechaMedicion, String ciudad, String pais,
                    double temperatura, double humedad) {
        this.sensorId = sensorId;
        this.fechaMedicion = fechaMedicion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    // 🔹 Getters y setters (requeridos por Spring Data Cassandra)
    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public Date getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(Date fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    @Override
    public String toString() {
        return "Medicion{" +
                "sensorId=" + sensorId +
                ", fechaMedicion=" + fechaMedicion +
                ", ciudad='" + ciudad + '\'' +
                ", pais='" + pais + '\'' +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }
}
