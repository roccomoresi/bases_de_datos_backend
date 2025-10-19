package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@Service
public class MedicionGeneratorService {

    private final MedicionRepository medicionRepository;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();

    public MedicionGeneratorService(MedicionRepository medicionRepository, SensorRepository sensorRepository) {
        this.medicionRepository = medicionRepository;
        this.sensorRepository = sensorRepository;
    }

    public String generarMediciones(int cantidadPorSensor) {
        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) {
            return "⚠️ No hay sensores registrados.";
        }

        for (Sensor sensor : sensores) {
            for (int i = 0; i < cantidadPorSensor; i++) {
                Medicion medicion = new Medicion();
                medicion.setSensorId(sensor.getId());
                medicion.setCiudad(sensor.getCiudad());
                medicion.setPais(sensor.getPais());
                medicion.setFechaMedicion(generarFechaAleatoria());
                medicion.setTemperatura(generarTemperaturaBase(sensor.getTipo()));
                medicion.setHumedad(40 + random.nextDouble() * 40);
                medicionRepository.save(medicion);
            }
        }

        return "✅ " + (cantidadPorSensor * sensores.size()) + " mediciones generadas correctamente.";
    }

    private Instant generarFechaAleatoria() {
        // últimos 7 días
        long ahora = Instant.now().toEpochMilli();
        long hace7Dias = ahora - (7L * 24 * 60 * 60 * 1000);
        long randomTime = hace7Dias + (long) (Math.random() * (ahora - hace7Dias));
        return Instant.ofEpochMilli(randomTime);
    }

    private double generarTemperaturaBase(String tipo) {
        double base = switch (tipo.toLowerCase()) {
            case "temperatura" -> 20 + random.nextDouble() * 10; // 20–30°C
            case "humedad" -> 15 + random.nextDouble() * 8;     // si querés
            default -> 18 + random.nextDouble() * 5;
        };
        return Math.round(base * 10.0) / 10.0;
    }
}
