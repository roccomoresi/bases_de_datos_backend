package com.example.persistencia.poliglota.utils;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class MedicionGeneratorService {

    private final MedicionService medicionService;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();
    private long ciclosEjecutados = 0;

    public MedicionGeneratorService(MedicionService medicionService, SensorRepository sensorRepository) {
        this.medicionService = medicionService;
        this.sensorRepository = sensorRepository;
    }

    /**
     * 🔄 Genera mediciones automáticas cada 10 segundos con algunas anomalías aleatorias.
     */
    @Scheduled(fixedRateString = "${mediciones.fixed-rate-ms:30000}")
    public void generarMedicionesAutomaticas() {
        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) {
            System.out.println("⚠️ No hay sensores cargados en Cassandra.");
            return;
        }

        ciclosEjecutados++;
        System.out.println("⏱️ Ciclo #" + ciclosEjecutados + " — Generando mediciones...");

        for (int i = 0; i < sensores.size(); i++) {
            Sensor sensor = sensores.get(i);
            try {
                double temperatura = generarTemperatura(sensor.getPais());
                double humedad = generarHumedad(sensor.getPais());

                Instant fechaMedicion = Instant.now()
                        .minus(i, ChronoUnit.HOURS)
                        .minus(ciclosEjecutados, ChronoUnit.DAYS);

                Medicion medicion = new Medicion(
                        sensor.getId(),
                        fechaMedicion,
                        sensor.getCiudad(),
                        sensor.getPais(),
                        temperatura,
                        humedad
                );

                medicionService.guardar(medicion);

                if (temperatura > 45 || temperatura < 0 || humedad > 90 || humedad < 10) {
                    System.out.printf("🚨 Alerta simulada [%s] %.1f°C / %.1f%% (%s)%n",
                            sensor.getCiudad(), temperatura, humedad, sensor.getPais());
                } else if (random.nextDouble() < 0.05) {
                    System.out.printf("ℹ️ Pico leve [%s] %.1f°C / %.1f%%%n",
                            sensor.getCiudad(), temperatura, humedad);
                }

            } catch (Exception e) {
                System.err.println("❌ Error generando medición para " + sensor.getNombre() + ": " + e.getMessage());
            }
        }
    }

    private double generarTemperatura(String pais) {
        double base, variacion;
        switch (pais.toLowerCase()) {
            case "argentina" -> { base = 25; variacion = 8; }
            case "uruguay" -> { base = 23; variacion = 7; }
            case "chile" -> { base = 20; variacion = 9; }
            case "noruega" -> { base = 5; variacion = 6; }
            default -> { base = 22; variacion = 10; }
        }

        // 🌡️ 10% de probabilidad de generar un pico extremo
        if (random.nextDouble() < 0.10) {
            double spike = (random.nextBoolean() ? 1 : -1) * (15 + random.nextDouble() * 15);
            return base + spike;
        }

        return base + (random.nextDouble() * variacion * 2 - variacion);
    }

    private double generarHumedad(String pais) {
        double base, variacion;
        switch (pais.toLowerCase()) {
            case "argentina", "uruguay" -> { base = 60; variacion = 15; }
            case "chile" -> { base = 50; variacion = 20; }
            case "noruega" -> { base = 80; variacion = 10; }
            default -> { base = 55; variacion = 20; }
        }

        // 💧 7% de probabilidad de anomalía extrema
        if (random.nextDouble() < 0.07) {
            double spike = (random.nextBoolean() ? 1 : -1) * (30 + random.nextDouble() * 20);
            return Math.max(1, Math.min(100, base + spike));
        }

        return Math.max(1, Math.min(100, base + (random.nextDouble() * variacion * 2 - variacion)));
    }
}
