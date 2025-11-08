package com.example.persistencia.poliglota.dto;

import com.example.persistencia.poliglota.model.cassandra.Sensor;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SensorDTO {

    private UUID id;
    private String nombre;
    private String tipo;
    private String ciudad;
    private String pais;
    private double latitud;
    private double longitud;
    private String estado;
    private String fechaInicioEmision; // 🔹 ahora es String formateado

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public SensorDTO(UUID id, String nombre, String tipo, String ciudad, String pais,
                     double latitud, double longitud, String estado, String fechaInicioEmision) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ciudad = ciudad;
        this.pais = pais;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.fechaInicioEmision = fechaInicioEmision;
    }

    // 🔸 Crea el DTO desde la entidad
    public static SensorDTO fromEntity(Sensor s) {
        String fechaFormateada = s.getFechaInicioEmision() != null
                ? FORMATTER.format(s.getFechaInicioEmision())
                : null;

        return new SensorDTO(
                s.getId(),
                s.getNombre(),
                s.getTipo(),
                s.getCiudad(),
                s.getPais(),
                s.getLatitud(),
                s.getLongitud(),
                s.getEstado(),
                fechaFormateada
        );
    }

    // Getters
    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getEstado() { return estado; }
    public String getFechaInicioEmision() { return fechaInicioEmision; }
}
