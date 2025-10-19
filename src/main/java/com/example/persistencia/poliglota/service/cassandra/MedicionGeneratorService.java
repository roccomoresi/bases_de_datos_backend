package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
public class MedicionGeneratorService {

    private final MedicionRepository medicionRepository;
    private final SensorRepository sensorRepository;
    private final AlertaService alertaService;
    private final Random random = new Random();

    public MedicionGeneratorService(
            MedicionRepository medicionRepository,
            SensorRepository sensorRepository,
            AlertaService alertaService
    ) {
        this.medicionRepository = medicionRepository;
        this.sensorRepository = sensorRepository;
        this.alertaService = alertaService;
    }

    /**
     * Genera mediciones una sola vez para todos los sensores.
     * Limpia las mediciones anteriores antes de insertar nuevas.
     */
    public String generarMedicionesUnaVez() {
        // 🧹 1️⃣ Limpiar todas las mediciones previas antes de generar nuevas
        medicionRepository.deleteAll();
        System.out.println("🧹 Mediciones anteriores eliminadas de Cassandra.");

        // 🧠 2️⃣ Obtener sensores registrados
        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) {
            return "⚠️ No hay sensores registrados.";
        }

        int total = 0;

        // 🔁 3️⃣ Generar 10 mediciones por sensor
        for (Sensor sensor : sensores) {
            for (int i = 0; i < 10; i++) {
                Medicion medicion = new Medicion();
                medicion.setSensorId(sensor.getId());
                medicion.setCiudad(sensor.getCiudad());
                medicion.setPais(sensor.getPais());
                medicion.setFechaMedicion(generarFechaAleatoria());

                // 🌡️ Generar valores de temperatura y humedad
                medicion.setTemperatura(20 + random.nextDouble() * 20); // 20–40°C
                medicion.setHumedad(15 + random.nextDouble() * 70);     // 15–85%

                medicionRepository.save(medicion);
                total++;

                // 🚨 Crear alertas si hay valores fuera de rango
                if (medicion.getTemperatura() > 35) {
                    alertaService.crear(
                            sensor.getId(),
                            "climatica",
                            "🔥 Temperatura alta en " + sensor.getCiudad() +
                                    ": " + medicion.getTemperatura() + "°C",
                            sensor.getCiudad(),
                            sensor.getPais()
                    );
                }

                if (medicion.getHumedad() < 25) {
                    alertaService.crear(
                            sensor.getId(),
                            "climatica",
                            "💧 Humedad baja en " + sensor.getCiudad() +
                                    ": " + medicion.getHumedad() + "%",
                            sensor.getCiudad(),
                            sensor.getPais()
                    );
                }
            }
        }

        return "✅ " + total + " mediciones generadas correctamente (limpiando las anteriores).";
    }

    private Instant generarFechaAleatoria() {
        long ahora = Instant.now().toEpochMilli();
        long hace7Dias = ahora - (7L * 24 * 60 * 60 * 1000);
        long randomTime = hace7Dias + (long) (Math.random() * (ahora - hace7Dias));
        return Instant.ofEpochMilli(randomTime);
    }
}
