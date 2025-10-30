package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorPais;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorCiudadRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorPaisRepository;
import com.example.persistencia.poliglota.service.intergracion.AlertaMongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MedicionService {

    private static final Logger log = LoggerFactory.getLogger(MedicionService.class);

    private final MedicionRepository medicionRepository;
    private final MedicionPorCiudadRepository medicionPorCiudadRepository;
    private final MedicionPorPaisRepository medicionPorPaisRepository;
    private final AlertaMongoClient alertaMongoClient;

    public MedicionService(
            MedicionRepository medicionRepository,
            MedicionPorCiudadRepository medicionPorCiudadRepository,
            MedicionPorPaisRepository medicionPorPaisRepository,
            AlertaMongoClient alertaMongoClient
    ) {
        this.medicionRepository = medicionRepository;
        this.medicionPorCiudadRepository = medicionPorCiudadRepository;
        this.medicionPorPaisRepository = medicionPorPaisRepository;
        this.alertaMongoClient = alertaMongoClient;
    }

    // 🔹 Obtener todas las mediciones por sensor
    public List<Medicion> obtenerPorSensor(UUID sensorId) {
        log.info("📊 Buscando mediciones para sensor {}", sensorId);
        return medicionRepository.findBySensorId(sensorId);
    }

    // 🔹 Obtener mediciones de un sensor dentro de un rango de fechas
    public List<Medicion> obtenerPorSensorYRangoFechas(UUID sensorId, String desdeStr, String hastaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            // Normalizamos formato
            String desdeCompleto = desdeStr.contains("T") ? desdeStr : desdeStr + "T00:00:00";
            String hastaCompleto = hastaStr.contains("T") ? hastaStr : hastaStr + "T23:59:59";

            Date desde = sdf.parse(desdeCompleto);
            Date hasta = sdf.parse(hastaCompleto);

            log.info("📅 Rango solicitado: {} - {}", desde, hasta);
            return medicionRepository.findBySensorIdAndFechaMedicionBetween(sensorId, desde, hasta);
        } catch (Exception e) {
            log.error("❌ Error al parsear fechas: {}", e.getMessage());
            throw new RuntimeException("Error al parsear fechas: " + e.getMessage());
        }
    }

    // 🔹 Obtener mediciones por ciudad y país
    public List<MedicionPorCiudad> obtenerPorCiudad(String ciudad, String pais) {
        log.info("🌎 Buscando mediciones por ciudad={} y pais={}", ciudad, pais);
        return medicionPorCiudadRepository.findByCiudadAndPais(ciudad, pais);
    }

public Medicion guardar(Medicion medicion) {
    try {
        // 🕒 Manejo robusto de la fecha
        Date fechaFinal;

        Object fecha = medicion.getFechaMedicion();
        if (fecha == null) {
            fechaFinal = new Date(); // genera ahora
        } else if (fecha instanceof Instant) {
            fechaFinal = Date.from((Instant) fecha);
        } else if (fecha instanceof Date) {
            fechaFinal = (Date) fecha;
        } else if (fecha instanceof String) {
            fechaFinal = Date.from(Instant.parse((String) fecha));
        } else {
            throw new IllegalArgumentException("Tipo de fecha no soportado: " + fecha.getClass());
        }

        // ✅ Asignar la fecha calculada al objeto antes de guardar
        medicion.setFechaMedicion(fechaFinal);

        // 🧱 Guarda en la tabla desnormalizada por ciudad
        MedicionPorCiudad medicionCiudad = new MedicionPorCiudad(
            medicion.getCiudad(),
            medicion.getPais(),
            fechaFinal,
            medicion.getSensorId(),
            medicion.getTemperatura(),
            medicion.getHumedad()
        );
        medicionPorCiudadRepository.save(medicionCiudad);

        // 🧱 Guarda en la tabla desnormalizada por país
        MedicionPorPais medicionPais = new MedicionPorPais(
            medicion.getPais(),
            fechaFinal,
            medicion.getCiudad(),
            medicion.getSensorId(),
            medicion.getTemperatura(),
            medicion.getHumedad()
        );
        medicionPorPaisRepository.save(medicionPais);

        // 🗃 Guarda también en la tabla principal de mediciones
        Medicion guardada = medicionRepository.save(medicion);

        // ⚠️ Dispara alertas en Mongo
        evaluarAlertasAutomaticas(guardada);

        log.info("✅ Medición guardada correctamente para sensor {}", medicion.getSensorId());
        return guardada;

    } catch (Exception e) {
        log.error("❌ Error al guardar medición: {}", e.getMessage(), e);
        throw new RuntimeException("Error al guardar medición: " + e.getMessage());
    }
}



    // 🔹 Eliminar medición
    public void eliminar(Medicion medicion) {
        log.warn("🗑️ Eliminando medición de sensor {}", medicion.getSensorId());
        medicionRepository.delete(medicion);
    }

     private void evaluarAlertasAutomaticas(Medicion medicion) {
        try {
            if (medicion.getTemperatura() != null && medicion.getTemperatura() > 35.0) {
                alertaMongoClient.enviarAlerta(
                    medicion.getSensorId(),
                    "climatica",
                    "🌡️ Temperatura alta en " + medicion.getCiudad() + ": " + medicion.getTemperatura() + "°C",
                    medicion.getCiudad(),
                    medicion.getPais(),
                    medicion.getTemperatura(),
                    medicion.getHumedad(),
                    "alta"
                );
            }

            if (medicion.getHumedad() != null && medicion.getHumedad() < 20.0) {
                alertaMongoClient.enviarAlerta(
                    medicion.getSensorId(),
                    "climatica",
                    "💧 Humedad baja en " + medicion.getCiudad() + ": " + medicion.getHumedad() + "%",
                    medicion.getCiudad(),
                    medicion.getPais(),
                    medicion.getTemperatura(),
                    medicion.getHumedad(),
                    "media"
                );
            }

        } catch (Exception e) {
            log.error("❌ Error al evaluar alertas automáticas: {}", e.getMessage());
        }
    }
}

