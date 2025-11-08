package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.*;
import com.example.persistencia.poliglota.repository.cassandra.*;
import com.example.persistencia.poliglota.service.intergracion.AlertaMongoClient;
import com.example.persistencia.poliglota.utils.CassandraBucketUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MedicionService {

    private static final Logger log = LoggerFactory.getLogger(MedicionService.class);

    private final MedicionPorSensorRepository medicionPorSensorRepository;
    private final MedicionPorCiudadRepository medicionPorCiudadRepository;
    private final MedicionPorPaisRepository medicionPorPaisRepository;
    private final MedicionPorRangoGlobalRepository medicionPorRangoGlobalRepository;
    private final CassandraOperations cassandraOperations;
    private final AlertaMongoClient alertaMongoClient;
    private final SensorRepository sensorRepository;

public MedicionService(
    MedicionPorSensorRepository medicionPorSensorRepository,
    MedicionPorCiudadRepository medicionPorCiudadRepository,
    MedicionPorPaisRepository medicionPorPaisRepository,
    MedicionPorRangoGlobalRepository medicionPorRangoGlobalRepository,
    CassandraOperations cassandraOperations,
    AlertaMongoClient alertaMongoClient,
    SensorRepository sensorRepository // 👈 nuevo
) {
    this.medicionPorSensorRepository = medicionPorSensorRepository;
    this.medicionPorCiudadRepository = medicionPorCiudadRepository;
    this.medicionPorPaisRepository = medicionPorPaisRepository;
    this.medicionPorRangoGlobalRepository = medicionPorRangoGlobalRepository;
    this.cassandraOperations = cassandraOperations;
    this.alertaMongoClient = alertaMongoClient;
    this.sensorRepository = sensorRepository;
}


    // 📊 Obtener todas las mediciones por sensor
    public List<MedicionPorSensor> obtenerPorSensor(UUID sensorId) {
        log.info("📊 Buscando mediciones para sensor {}", sensorId);
        return medicionPorSensorRepository.findBySensorId(sensorId);
    }

    public List<MedicionPorPais> obtenerPorPais(String pais) {
    log.info("🌎 Buscando mediciones por país={}", pais);
    return medicionPorPaisRepository.findByPais(pais);
}




    // 📅 Obtener mediciones de un sensor dentro de un rango de fechas
    public List<MedicionPorSensor> obtenerPorSensorYRangoFechas(UUID sensorId, String desdeStr, String hastaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date desde = sdf.parse(desdeStr.contains("T") ? desdeStr : desdeStr + "T00:00:00");
            Date hasta = sdf.parse(hastaStr.contains("T") ? hastaStr : hastaStr + "T23:59:59");

            log.info("📅 Rango solicitado: {} - {}", desde, hasta);
            return medicionPorSensorRepository.findBySensorIdAndFechaMedicionBetween(sensorId, desde, hasta);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear fechas: " + e.getMessage());
        }
    }

    // 🌍 Obtener mediciones por ciudad y rango
    public List<MedicionPorCiudad> obtenerPorCiudadYRango(String ciudad, String desdeStr, String hastaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date desde = sdf.parse(desdeStr.contains("T") ? desdeStr : desdeStr + "T00:00:00");
            Date hasta = sdf.parse(hastaStr.contains("T") ? hastaStr : hastaStr + "T23:59:59");
            return medicionPorCiudadRepository.findByCiudadAndFechaMedicionBetween(ciudad, desde, hasta);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear fechas: " + e.getMessage());
        }
    }

    // 🌎 Obtener mediciones por país y rango
    public List<MedicionPorPais> obtenerPorPaisYRango(String pais, String desdeStr, String hastaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date desde = sdf.parse(desdeStr.contains("T") ? desdeStr : desdeStr + "T00:00:00");
            Date hasta = sdf.parse(hastaStr.contains("T") ? hastaStr : hastaStr + "T23:59:59");
            return medicionPorPaisRepository.findByPaisAndFechaMedicionBetween(pais, desde, hasta);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear fechas: " + e.getMessage());
        }
    }

    // 🌐 Obtener rango global (usa buckets)
    public List<MedicionPorRangoGlobal> obtenerMedicionesRangoGlobal(LocalDateTime desde, LocalDateTime hasta) {
        List<String> buckets = CassandraBucketUtils.calcularBuckets(desde.toLocalDate(), hasta.toLocalDate());
        Date desdeDate = Date.from(desde.toInstant(ZoneOffset.UTC));
        Date hastaDate = Date.from(hasta.toInstant(ZoneOffset.UTC));

        return medicionPorRangoGlobalRepository.findByYearMonthBucketInAndFechaMedicionBetween(buckets, desdeDate, hastaDate);
    }

    // 💾 Guardar medición en las 4 tablas (BATCH LOGGED)
    public Medicion guardar(Medicion medicion) {
    try {
        // 🕒 Fecha final (usa actual si no viene)
        Date fechaFinal = medicion.getFechaMedicion() != null
                ? medicion.getFechaMedicion()
                : new Date();
        medicion.setFechaMedicion(fechaFinal);

        // 📦 Crear versión para cada tabla desnormalizada
        MedicionPorSensor medicionSensor = new MedicionPorSensor(
            medicion.getSensorId(),
            fechaFinal,
            medicion.getCiudad(),
            medicion.getPais(),
            medicion.getTemperatura(),
            medicion.getHumedad()
        );

        MedicionPorCiudad medicionCiudad = new MedicionPorCiudad(
            medicion.getCiudad(),
            fechaFinal,
            medicion.getSensorId(),
            medicion.getPais(),
            medicion.getTemperatura(),
            medicion.getHumedad()
        );

        MedicionPorPais medicionPais = new MedicionPorPais(
    medicion.getPais(),
    fechaFinal,
    medicion.getSensorId(),   // ✅ ahora va el UUID del sensor
    medicion.getCiudad(),     // ✅ ahora va la ciudad
    medicion.getTemperatura(),
    medicion.getHumedad()
);

        // 🗓️ Calcular bucket mensual (para particionar el rango global)
        String bucket = fechaFinal.toInstant()
                .atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        MedicionPorRangoGlobal medicionGlobal = new MedicionPorRangoGlobal(
            bucket,
            fechaFinal,
            medicion.getSensorId(),
            medicion.getCiudad(),
            medicion.getPais(),
            medicion.getTemperatura(),
            medicion.getHumedad()
        );

        // 🧩 Batch atómico (escribe las 4 tablas en una sola operación)
        CassandraBatchOperations batch = cassandraOperations.batchOps();
        batch.insert(medicionSensor);
        batch.insert(medicionCiudad);
        batch.insert(medicionPais);
        batch.insert(medicionGlobal);
        batch.execute();

        // 🚨 Verifica alertas automáticas si aplica
        evaluarAlertasAutomaticas(medicion);

        log.info("✅ Medición guardada en 4 tablas para sensor {}", medicion.getSensorId());
        return medicion;

    } catch (Exception e) {
        log.error("❌ Error al guardar medición: {}", e.getMessage(), e);
        throw new RuntimeException("Error al guardar medición: " + e.getMessage());
    }
}


    private void evaluarAlertasAutomaticas(Medicion medicion) {
    try {
        boolean alertaGenerada = false;

        // 🔥 Temperatura muy alta
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
            alertaGenerada = true;
        }

        // 💧 Humedad baja
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
            alertaGenerada = true;
        }

        // ⚠️ Valores imposibles = sensor fallando
        if (medicion.getTemperatura() != null &&
            (medicion.getTemperatura() < -40 || medicion.getTemperatura() > 90)) {

            alertaMongoClient.enviarAlerta(
                medicion.getSensorId(),
                "sensor",
                "⚠️ Sensor " + medicion.getSensorId() + " reportó valores anómalos: "
                        + medicion.getTemperatura() + "°C",
                medicion.getCiudad(),
                medicion.getPais(),
                medicion.getTemperatura(),
                medicion.getHumedad(),
                "critica"
            );

            sensorRepository.updateEstado(medicion.getSensorId(), "falla");
            alertaGenerada = true;
        }

        if (alertaGenerada) {
            log.warn("🚨 Se generó alerta automática para sensor {}", medicion.getSensorId());
        }

    } catch (Exception e) {
        log.error("❌ Error al evaluar alertas automáticas: {}", e.getMessage());
    }
}


    }

