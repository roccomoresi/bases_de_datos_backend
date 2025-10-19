package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class AlertaService {

    private final AlertaRepository repository;

    public AlertaService(AlertaRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------------------
    // 🌡️ CREACIÓN INTELIGENTE (usada por Cassandra)
    // ---------------------------------------------------------------------
    public Alerta crear(UUID sensorId, String tipo, String descripcion, String ciudad, String pais) {
        // Genera una alerta simple cuando no hay detalles específicos
        return crearConDetalles(sensorId, tipo, descripcion, ciudad, pais, Map.of("fuente", "cassandra"));
    }

    // ---------------------------------------------------------------------
    // 🧩 CREACIÓN CON DETALLES (más completa y flexible)
    // ---------------------------------------------------------------------
    public Alerta crearConDetalles(UUID sensorId, String tipo, String descripcion,
                                   String ciudad, String pais, Map<String, Object> detalles) {

        // Detecta severidad considerando temperatura, humedad y fallas
        String severidad = calcularSeveridad(detalles);
        String color = obtenerColor(severidad);
        String icono = obtenerIcono(tipo);
        String fuente = (String) detalles.getOrDefault("fuente", "manual");

        // Instancia la alerta
        Alerta alerta = new Alerta(
                UUID.randomUUID(),
                tipo,
                sensorId,
                ciudad,
                pais,
                Instant.now(),
                descripcion,
                "activa",
                severidad,
                color,
                icono,
                fuente,
                detalles
        );

        return repository.save(alerta);
    }



public int eliminarTodas() {
    int cantidad = repository.findAll().size();
    repository.deleteAll();
    return cantidad;
}


    // ---------------------------------------------------------------------
    // 🎚️ CÁLCULO DE SEVERIDAD (multi-condición)
    // ---------------------------------------------------------------------
    private String calcularSeveridad(Map<String, Object> detalles) {
        double temp = ((Number) detalles.getOrDefault("temperatura", 0)).doubleValue();
        double hum = ((Number) detalles.getOrDefault("humedad", 50)).doubleValue();
        String estadoSensor = (String) detalles.getOrDefault("estadoSensor", "OK");

        // Casos de falla del sensor
        if ("FALLA".equalsIgnoreCase(estadoSensor)) return "critica";
        // Casos climáticos severos
        if (temp > 40 || hum < 15) return "critica";
        if (temp > 30 || hum < 25) return "moderada";
        return "baja";
    }

    // ---------------------------------------------------------------------
    // 🎨 COLOR SEGÚN SEVERIDAD (para visualización en frontend)
    // ---------------------------------------------------------------------
    private String obtenerColor(String severidad) {
        return switch (severidad) {
            case "critica" -> "#F44336"; // rojo
            case "moderada" -> "#FFC107"; // amarillo
            default -> "#4CAF50"; // verde
        };
    }

    // ---------------------------------------------------------------------
    // 🔧 ICONO SEGÚN TIPO DE ALERTA
    // ---------------------------------------------------------------------
    private String obtenerIcono(String tipo) {
        return switch (tipo) {
            case "climatica" -> "🌡️";
            case "funcionamiento" -> "⚙️";
            default -> "📡"; // por defecto, alertas de sensor
        };
    }

    // ---------------------------------------------------------------------
    // 📋 LISTAR TODAS LAS ALERTAS
    // ---------------------------------------------------------------------
    public List<Alerta> listar() {
        return repository.findAll();
    }

    // ---------------------------------------------------------------------
    // 🟢 LISTAR SOLO LAS ACTIVAS
    // ---------------------------------------------------------------------
    public List<Alerta> listarActivas() {
        return repository.findByEstado("activa");
    }

    // ---------------------------------------------------------------------
    // ✅ RESOLVER ALERTA (cambiar estado a “resuelta”)
    // ---------------------------------------------------------------------
    public Alerta resolver(UUID id) {
        Alerta alerta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la alerta con ID " + id));

        alerta.setEstado("resuelta");
        alerta.getDetalles().put("fecha_resolucion", Instant.now().toString());
        alerta.getDetalles().put("resuelta_por", "sistema");

        return repository.save(alerta);
    }

    // ---------------------------------------------------------------------
    // 🔍 BUSCAR ALERTAS POR CIUDAD Y PAÍS
    // ---------------------------------------------------------------------
    public List<Alerta> buscarPorUbicacion(String ciudad, String pais) {
        return repository.findByCiudadAndPais(ciudad, pais);
    }

    // ---------------------------------------------------------------------
    // 🗑️ ELIMINAR ALERTA
    // ---------------------------------------------------------------------
    public void eliminar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No existe alerta con ID " + id);
        }
        repository.deleteById(id);
    }
}
