package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
public class MedicionSchedulerService {

    private final MedicionRepository medicionRepository;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();

    public MedicionSchedulerService(MedicionRepository medicionRepository, SensorRepository sensorRepository) {
        this.medicionRepository = medicionRepository;
        this.sensorRepository = sensorRepository;
    }

    // 🔁 Genera una nueva medición cada 10 segundos
    @Scheduled(fixedRate = 10000)
    public void generarMedicionesAutomaticas() {
        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) return;

        for (Sensor sensor : sensores) {
            Medicion medicion = new Medicion();
            medicion.setSensorId(sensor.getId());
            medicion.setCiudad(sensor.getCiudad());
            medicion.setPais(sensor.getPais());
            medicion.setFechaMedicion(Instant.now());

            // genera variaciones naturales
            medicion.setTemperatura(generarTemperatura(sensor.getTipo()));
            medicion.setHumedad(40 + random.nextDouble() * 40);

            medicionRepository.save(medicion);
            System.out.println("🌡️ Nueva medición generada para " + sensor.getNombre());
        }
    }

    private double generarTemperatura(String tipo) {
        double base = switch (tipo.toLowerCase()) {
            case "temperatura" -> 20 + random.nextDouble() * 10; // 20–30°C
            case "humedad" -> 15 + random.nextDouble() * 8;
            default -> 18 + random.nextDouble() * 5;
        };
        return Math.round(base * 10.0) / 10.0;
    }
}
