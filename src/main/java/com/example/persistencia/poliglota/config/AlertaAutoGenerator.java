package com.example.persistencia.poliglota.config;

import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;


import java.util.*;

/**
 * 🔁 Generador automático y periódico de alertas en MongoDB
 *    Basado en sensores cargados en Cassandra.
 */
@Component
public class AlertaAutoGenerator {

    private final SensorRepository sensorRepository;
    private final AlertaService alertaService;
    private final Random random = new Random();

    // 🔧 Activar o desactivar modo demo
    private static final boolean MODO_DEMO = true;

    public AlertaAutoGenerator(SensorRepository sensorRepository, AlertaService alertaService) {
        this.sensorRepository = sensorRepository;
        this.alertaService = alertaService;
        System.out.println("🧩 [DEBUG] AlertaAutoGenerator registrado correctamente por Spring");
    }

@PostConstruct
public void generarInicial() {
    if (!MODO_DEMO) return;

    try {
        Thread.sleep(2000); // Espera breve por seguridad
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    System.out.println("🚀 [INIT] Generador automático de alertas Cassandra → Mongo iniciado...");

    try {
        // 🔹 Eliminar alertas previas para empezar limpio
        int eliminadas = alertaService.eliminarTodas();
        if (eliminadas > 0) {
            System.out.println("🧹 [CLEANUP] Se eliminaron " + eliminadas + " alertas previas.");
        }

        List<Sensor> sensores = sensorRepository.findAll();
        if (sensores.isEmpty()) {
            System.out.println("⚠️ [WARN] No se encontraron sensores en Cassandra.");
            return;
        }

        sensores.stream()
                .filter(s -> "activo".equalsIgnoreCase(s.getEstado()))
                .forEach(sensor -> crearAlertaParaSensor(sensor, false));

        System.out.println("🎯 [DONE] Alertas iniciales regeneradas correctamente.");

    } catch (Exception e) {
        System.err.println("❌ [ERROR] Falló la generación automática de alertas: " + e.getMessage());
    }
}



    // ---------------------------------------------------------------------
    // 🔹 Método que realmente crea la alerta
    // ---------------------------------------------------------------------
    private void crearAlertaParaSensor(Sensor sensor, boolean esDemo) {
        if (sensor == null || sensor.getId() == null) return;

        double valor = generarValor(sensor.getTipo());
        Map<String, Object> detalles = new HashMap<>();
        detalles.put(sensor.getTipo(), valor);
        detalles.put("fuente", "cassandra");
        detalles.put("demo", esDemo);

        String descripcion = generarDescripcion(sensor, valor);

        alertaService.crearConDetalles(
                sensor.getId(),
                "climatica",
                descripcion,
                sensor.getCiudad(),
                sensor.getPais(),
                detalles
        );

        System.out.printf("✅ [%s] Alerta creada para sensor '%s' (%s, %s) — %s %.1f%n",
                esDemo ? "DEMO" : "INIT",
                sensor.getNombre(),
                sensor.getCiudad(),
                sensor.getPais(),
                sensor.getTipo(),
                valor
        );
    }

    // ---------------------------------------------------------------------
    // 🔸 Genera valores controlados según el tipo de sensor
    // ---------------------------------------------------------------------
    private double generarValor(String tipo) {
        if (tipo == null) return 0.0;
        return tipo.equalsIgnoreCase("temperatura")
                ? 20 + random.nextDouble() * 25  // 20°C a 45°C
                : 30 + random.nextDouble() * 50; // 30% a 80% humedad
    }

    private String generarDescripcion(Sensor sensor, double valor) {
        String tipo = sensor.getTipo() != null ? sensor.getTipo().toLowerCase() : "sensor";
        String ciudad = sensor.getCiudad() != null ? sensor.getCiudad() : "desconocida";

        return switch (tipo) {
            case "temperatura" ->
                    "🌡️ Temperatura actual en " + ciudad + ": " + String.format("%.1f", valor) + "°C";
            case "humedad" ->
                    "💧 Humedad actual en " + ciudad + ": " + String.format("%.1f", valor) + "%";
            default ->
                    "📡 Lectura simulada de " + sensor.getNombre() + " (" + tipo + ")";
        };
    }


  // ---------------------------------------------------------------------
    // 🧹 Limpieza automática de alertas demo viejas
    // ---------------------------------------------------------------------
    @Scheduled(fixedRate = 300000) // cada 5 minutos
    public void limpiarAlertasDemo() {
        if (!MODO_DEMO) return;
        try {
            int eliminadas = alertaService.eliminarAlertasDemo();
            if (eliminadas > 0)
                System.out.println("🧹 [CLEANUP] Se eliminaron " + eliminadas + " alertas demo antiguas.");
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] Error al limpiar alertas demo: " + e.getMessage());
        }
    }

}
