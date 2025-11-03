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

    // contador interno para simular el paso de los días
    private long ciclosEjecutados = 0;
    

    public MedicionGeneratorService(MedicionService medicionService, SensorRepository sensorRepository) {
        this.medicionService = medicionService;
        this.sensorRepository = sensorRepository;
    }

    /**
     * 🔄 Genera mediciones automáticas cada 10 segundos
     * con fechas simuladas distribuidas entre horas y días.
     */
    @Scheduled(fixedRate = 10000)
    public void generarMedicionesAutomaticas() {
        List<Sensor> sensores = sensorRepository.findAll();

        if (sensores.isEmpty()) {
            System.out.println("⚠️ No hay sensores cargados en Cassandra.");
            return;
        }

        ciclosEjecutados++;
        System.out.println("⏱️ Ciclo de generación #" + ciclosEjecutados);

        for (int i = 0; i < sensores.size(); i++) {
            Sensor sensor = sensores.get(i);
            try {
                double temperatura = generarTemperatura(sensor.getPais());
                double humedad = generarHumedad(sensor.getPais());

                // 🕒 Fecha simulada: cada sensor separado 1 hora, más offset diario según ciclo
                Instant fechaMedicion = Instant.now()
                        .minus(i, ChronoUnit.HOURS)       // cada sensor 1 hora antes
                        .minus(ciclosEjecutados, ChronoUnit.DAYS); // cada ciclo, un día atrás

                Medicion medicion = new Medicion(
                        sensor.getId(),
                        fechaMedicion,
                        sensor.getCiudad(),
                        sensor.getPais(),
                        temperatura,
                        humedad
                );

                medicionService.guardar(medicion);
                System.out.printf("🌡️ [%s] %s | %.1f°C | %.1f%% | fecha=%s%n",
                        sensor.getNombre(), sensor.getCiudad(),
                        temperatura, humedad, fechaMedicion);

            } catch (Exception e) {
                System.err.println("❌ Error generando medición para " + sensor.getNombre() + ": " + e.getMessage());
            }
        }
    }

    private double generarTemperatura(String pais) {
    final double MIN_TEMP = 1.0;
    final double MAX_TEMP = 60.0;
    final double RANGO_TOTAL = MAX_TEMP - MIN_TEMP;

    return switch (pais.toLowerCase()) {
        case "argentina" -> MIN_TEMP + random.nextDouble() * RANGO_TOTAL;
        case "uruguay" -> MIN_TEMP + random.nextDouble() * (RANGO_TOTAL * 0.9);
        case "chile" -> MIN_TEMP + random.nextDouble() * RANGO_TOTAL;
        case "noruega" -> MIN_TEMP + random.nextDouble() * (RANGO_TOTAL * 0.6) + (RANGO_TOTAL * 0.4);
        default -> MIN_TEMP + random.nextDouble() * RANGO_TOTAL;
    };
}

private double generarHumedad(String pais) {
    final double MIN_HUM = 1.0;
    final double MAX_HUM = 100.0;
    final double RANGO_TOTAL = MAX_HUM - MIN_HUM;

    return switch (pais.toLowerCase()) {
        case "argentina", "uruguay" -> MIN_HUM + random.nextDouble() * RANGO_TOTAL;
        case "chile" -> MIN_HUM + random.nextDouble() * (RANGO_TOTAL * 0.7);
        case "noruega" -> MIN_HUM + random.nextDouble() * (RANGO_TOTAL * 0.3) + (RANGO_TOTAL * 0.7);
        default -> MIN_HUM + random.nextDouble() * RANGO_TOTAL;
    };
    
}

}
