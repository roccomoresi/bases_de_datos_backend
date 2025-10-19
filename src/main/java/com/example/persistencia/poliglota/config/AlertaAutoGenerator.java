package com.example.persistencia.poliglota.config;

import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Generador automático de alertas en MongoDB basado en sensores de Cassandra.
 * Se ejecuta al iniciar la aplicación.
 */
@Component
public class AlertaAutoGenerator implements CommandLineRunner {

    private final SensorRepository sensorRepository;
    private final AlertaService alertaService;
    private final Random random = new Random();

    public AlertaAutoGenerator(SensorRepository sensorRepository, AlertaService alertaService) {
        this.sensorRepository = sensorRepository;
        this.alertaService = alertaService;
    }

    @Override
    public void run(String... args) {
        System.out.println("🚀 [INIT] Generador automático de alertas Cassandra → Mongo iniciado...");

        try {
            // ⚙️ 1. Verificar si ya existen alertas
            var existentes = alertaService.listar();
            if (!existentes.isEmpty()) {
                System.out.println("ℹ️ [INFO] Ya existen alertas en MongoDB (" + existentes.size() + "). No se generarán nuevas.");
                return;
            }

            // ⚙️ 2. Leer sensores desde Cassandra
            List<Sensor> sensores = sensorRepository.findAll();
            if (sensores.isEmpty()) {
                System.out.println("⚠️ [WARN] No se encontraron sensores en Cassandra.");
                return;
            }

            // ⚙️ 3. Crear alertas automáticas
            sensores.stream()
                    .filter(s -> "activo".equalsIgnoreCase(s.getEstado()))
                    .forEach(sensor -> {
                        double valor = generarValor(sensor.getTipo());
                        Map<String, Object> detalles = new HashMap<>();
                        detalles.put(sensor.getTipo(), valor);
                        detalles.put("fuente", "cassandra");
                        detalles.put("umbralMax", 35);

                        String descripcion = generarDescripcion(sensor, valor);
                        alertaService.crearConDetalles(
                                sensor.getId(),
                                "climatica",
                                descripcion,
                                sensor.getCiudad(),
                                sensor.getPais(),
                                detalles
                        );

                        System.out.printf("✅ [OK] Alerta creada para %s (%s, %s)%n",
                                sensor.getNombre(), sensor.getCiudad(), sensor.getPais());
                    });

            System.out.println("🎯 [DONE] Alertas iniciales generadas exitosamente.");

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Falló la generación automática de alertas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    // 🔹 Métodos auxiliares
    // --------------------------------------------------------------

    private double generarValor(String tipo) {
        if (tipo == null) return 0.0;
        return tipo.equalsIgnoreCase("temperatura")
                ? 25 + random.nextDouble() * 20 // entre 25°C y 45°C
                : 20 + random.nextDouble() * 60; // entre 20% y 80% humedad
    }

    private String generarDescripcion(Sensor sensor, double valor) {
        String tipo = sensor.getTipo();
        String ciudad = sensor.getCiudad();

        if (tipo == null) tipo = "sensor";
        if (ciudad == null) ciudad = "desconocida";

        return switch (tipo.toLowerCase()) {
            case "temperatura" ->
                    "🌡️ Temperatura actual en " + ciudad + ": " + String.format("%.1f", valor) + "°C";
            case "humedad" ->
                    "💧 Humedad actual en " + ciudad + ": " + String.format("%.1f", valor) + "%";
            default ->
                    "📡 Lectura de " + sensor.getNombre() + " (" + tipo + ")";
        };
    }
}
