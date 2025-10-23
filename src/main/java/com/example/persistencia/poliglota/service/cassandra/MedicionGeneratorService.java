package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 🌡️ Servicio que genera mediciones automáticas en Cassandra
 * y dispara alertas en MongoDB cuando se superan umbrales críticos.
 * Además, limpia mediciones antiguas para evitar sobrecarga.
 */
@Service
public class MedicionGeneratorService {

    private final MedicionService medicionService;
    private final SensorRepository sensorRepository;
    private final AlertaService alertaService;
    private final Random random = new Random();

    // Configuración
    private static final boolean MODO_AUTOMATICO = true;
    private static final double UMBRAL_TEMPERATURA = 40.0; // °C
    private static final double UMBRAL_HUMEDAD = 25.0;     // %
    private static final int MAX_MEDICIONES_POR_SENSOR = 20; // límite de registros por sensor

    public MedicionGeneratorService(MedicionService medicionService,
                                    SensorRepository sensorRepository,
                                    AlertaService alertaService) {
        this.medicionService = medicionService;
        this.sensorRepository = sensorRepository;
        this.alertaService = alertaService;
    }

    // 🔁 Se ejecuta automáticamente cada 2 minutos
    @Scheduled(fixedRate = 120000) // 120000 ms = 2 min
    public void generarMedicionesAutomaticas() {
        if (!MODO_AUTOMATICO) return;

        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) {
            System.out.println("⚠️ [WARN] No hay sensores activos para generar mediciones.");
            return;
        }

        System.out.println("🌡️ [AUTO] Generando mediciones periódicas... (" + sensores.size() + " sensores)");

        sensores.stream()
                .filter(s -> "activo".equalsIgnoreCase(s.getEstado()))
                .forEach(this::crearMedicionControlada);
    }

    // -------------------------------------------------------------------------
    // 📊 Crea medición y evalúa si dispara alerta
    // -------------------------------------------------------------------------
    private void crearMedicionControlada(Sensor sensor) {
        double temperatura = 15 + random.nextDouble() * 35; // 15-50 °C
        double humedad = 20 + random.nextDouble() * 70;     // 20-90 %

        Medicion medicion = new Medicion(
                sensor.getId(),
                new Date(),
                sensor.getCiudad(),
                sensor.getPais(),
                temperatura,
                humedad
        );

        medicionService.guardar(medicion);
        System.out.printf("✅ [CASSANDRA] Nueva medición (%s): %.1f°C / %.1f%%\n",
                sensor.getCiudad(), temperatura, humedad);

        // Verificar umbrales
        verificarYDispararAlerta(sensor, temperatura, humedad);

        // Limpiar exceso de mediciones
        limpiarMedicionesViejas(sensor.getId());
    }

    // -------------------------------------------------------------------------
    // 🚨 Dispara alertas si supera umbrales
    // -------------------------------------------------------------------------
    private void verificarYDispararAlerta(Sensor sensor, double temperatura, double humedad) {
        boolean tempAlta = temperatura > UMBRAL_TEMPERATURA;
        boolean humBaja = humedad < UMBRAL_HUMEDAD;

        if (tempAlta || humBaja) {
            Map<String, Object> detalles = new HashMap<>();
            detalles.put("temperatura", temperatura);
            detalles.put("humedad", humedad);
            detalles.put("fuente", "cassandra");
            detalles.put("sensor", sensor.getNombre());

            String descripcion = tempAlta && humBaja
                    ? String.format("🔥 Temp. alta (%.1f°C) y humedad baja (%.1f%%) en %s",
                        temperatura, humedad, sensor.getCiudad())
                    : tempAlta
                        ? String.format("🌡️ Temperatura extrema: %.1f°C en %s", temperatura, sensor.getCiudad())
                        : String.format("💧 Humedad fuera de rango: %.1f%% en %s", humedad, sensor.getCiudad());

            alertaService.crearConDetalles(
                    sensor.getId(),
                    "climatica",
                    descripcion,
                    sensor.getCiudad(),
                    sensor.getPais(),
                    detalles
            );

            System.out.println("🚨 [ALERTA] " + descripcion);
        }
    }

    // -------------------------------------------------------------------------
    // 🧹 Elimina mediciones viejas del sensor si supera el límite
    // -------------------------------------------------------------------------
    private void limpiarMedicionesViejas(UUID sensorId) {
        try {
            List<Medicion> mediciones = medicionService.obtenerPorSensor(sensorId);
            if (mediciones.size() > MAX_MEDICIONES_POR_SENSOR) {
                mediciones.sort(Comparator.comparing(Medicion::getFechaMedicion));
                List<Medicion> viejas = mediciones.subList(0, mediciones.size() - MAX_MEDICIONES_POR_SENSOR);
                viejas.forEach(m -> System.out.println("🧹 [CLEANUP] Eliminando medición vieja de " + m.getFechaMedicion()));
                viejas.forEach(medicionService::eliminar);
            }
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] Error limpiando mediciones viejas: " + e.getMessage());
        }
    }
}
