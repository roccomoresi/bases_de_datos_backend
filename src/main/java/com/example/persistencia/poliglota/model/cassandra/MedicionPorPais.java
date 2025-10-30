package com.example.persistencia.poliglota.model.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("mediciones_por_pais")
public class MedicionPorPais {

    @PrimaryKeyColumn(name = "pais", type = PrimaryKeyType.PARTITIONED)
    private String pais;

    @PrimaryKeyColumn(name = "fecha_medicion", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date fechaMedicion;

    @PrimaryKeyColumn(name = "ciudad", type = PrimaryKeyType.CLUSTERED)
    private String ciudad;

    @PrimaryKeyColumn(name = "sensor_id", type = PrimaryKeyType.CLUSTERED)
    private UUID sensorId;

    @Column private Double temperatura;
    @Column private Double humedad;

    public MedicionPorPais() {}

    public MedicionPorPais(String pais, Date fechaMedicion, String ciudad, UUID sensorId, Double temperatura, Double humedad) {
        this.pais = pais;
        this.fechaMedicion = fechaMedicion;
        this.ciudad = ciudad;
        this.sensorId = sensorId;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Date getFechaMedicion() { return fechaMedicion; }
    public void setFechaMedicion(Date fechaMedicion) { this.fechaMedicion = fechaMedicion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Double getHumedad() { return humedad; }
    public void setHumedad(Double humedad) { this.humedad = humedad; }
}


