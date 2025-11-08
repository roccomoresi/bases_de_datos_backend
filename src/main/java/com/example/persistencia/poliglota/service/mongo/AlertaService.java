package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AlertaService {

    private final AlertaRepository repository;
    private final SensorRepository sensorRepository;

public AlertaService(AlertaRepository repository, SensorRepository sensorRepository) {
    this.repository = repository;
    this.sensorRepository = sensorRepository;
}

    // 🌡️ CREACIÓN SIMPLE
    public Alerta crear(UUID sensorId, String tipo, String descripcion, String ciudad, String pais) {
        return crearConDetalles(sensorId, tipo, descripcion, ciudad, pais, Map.of("fuente", "cassandra"));
    }

    // 🧩 CREACIÓN CON DETALLES
    public Alerta crearConDetalles(UUID sensorId, String tipo, String descripcion,
                                   String ciudad, String pais, Map<String, Object> detalles) {
        String severidad = calcularSeveridad(detalles);
        String color = obtenerColor(severidad);
        String icono = obtenerIcono(tipo);
        String fuente = (String) detalles.getOrDefault("fuente", "manual");

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

    // 🎚️ Lógica de severidad
    private String calcularSeveridad(Map<String, Object> detalles) {
        double temp = ((Number) detalles.getOrDefault("temperatura", 0)).doubleValue();
        double hum = ((Number) detalles.getOrDefault("humedad", 50)).doubleValue();
        String estadoSensor = (String) detalles.getOrDefault("estadoSensor", "OK");

        if ("FALLA".equalsIgnoreCase(estadoSensor)) return "critica";
        if (temp > 40 || hum < 15) return "critica";
        if (temp > 30 || hum < 25) return "moderada";
        return "baja";
    }

    private String obtenerColor(String severidad) {
        return switch (severidad) {
            case "critica" -> "#F44336";
            case "moderada" -> "#FFC107";
            default -> "#4CAF50";
        };
    }

    private String obtenerIcono(String tipo) {
        return switch (tipo) {
            case "climatica" -> "🌡️";
            case "funcionamiento" -> "⚙️";
            default -> "📡";
        };
    }

public Alerta asignarTecnico(UUID id, Integer tecnicoId, String nombreTecnico) {
    Alerta alerta = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró la alerta con ID " + id));

    alerta.setTecnicoAsignado(tecnicoId);
    alerta.setNombreTecnico(nombreTecnico);
    alerta.setFechaAsignacion(Instant.now());
    alerta.getDetalles().put("asignado_por", "admin");
    alerta.getDetalles().put("asignado_en", Instant.now().toString());

    return repository.save(alerta);
}



    // ---------------------------------------------------------------------
    // 🔍 OPERACIONES BÁSICAS
    // ---------------------------------------------------------------------
    public List<Alerta> listar() {
        return repository.findAll();
    }

    public List<Alerta> listarActivas() {
        return repository.findByEstado("activa");
    }

    public Alerta resolver(UUID id) {
    Alerta alerta = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró la alerta con ID " + id));

    alerta.setEstado("resuelta");
    alerta.getDetalles().put("fecha_resolucion", Instant.now().toString());
    alerta.getDetalles().put("resuelta_por", "admin");

    // ✅ Si está asociada a un sensor, lo reactivamos en Cassandra
    if (alerta.getSensorId() != null) {
        try {
            sensorRepository.updateEstado(alerta.getSensorId(), "activo");
        } catch (Exception e) {
            alerta.getDetalles().put("warning", "No se pudo reactivar el sensor: " + e.getMessage());
        }
    }

    return repository.save(alerta);
}


    public List<Alerta> buscarPorUbicacion(String ciudad, String pais) {
        return repository.findByCiudadAndPais(ciudad, pais);
    }

    public void eliminar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No existe alerta con ID " + id);
        }
        repository.deleteById(id);
    }

    public int eliminarTodas() {
        int cantidad = repository.findAll().size();
        repository.deleteAll();
        return cantidad;
    }

    // ---------------------------------------------------------------------
    // 🧹 ELIMINAR ALERTAS DEMO
    // ---------------------------------------------------------------------
    public int eliminarAlertasDemo() {
        List<Alerta> demo = repository.findAll().stream()
                .filter(a -> {
                    Object flag = a.getDetalles().get("demo");
                    return flag != null && Boolean.parseBoolean(flag.toString());
                })
                .toList();

        repository.deleteAll(demo);
        return demo.size();
    }

    // ---------------------------------------------------------------------
    // 🔎 FILTRO FLEXIBLE
    // ---------------------------------------------------------------------
    public List<Alerta> filtrar(String tipo, String severidad, String ciudad, String pais) {
        // Si usás la versión con @Query en el repo:
        // return repository.filtrarAlertas(tipo, severidad, ciudad, pais);

        // 🔹 Si preferís hacerlo en memoria (seguro y funciona ya mismo):
        List<Alerta> todas = repository.findAll();

        return todas.stream()
                .filter(a -> tipo == null || a.getTipo().equalsIgnoreCase(tipo))
                .filter(a -> severidad == null || a.getSeveridad().equalsIgnoreCase(severidad))
                .filter(a -> ciudad == null ||
                        (a.getCiudad() != null && a.getCiudad().equalsIgnoreCase(ciudad)))
                .filter(a -> pais == null ||
                        (a.getPais() != null && a.getPais().equalsIgnoreCase(pais)))
                .toList();
    }

    public List<Alerta> listarResueltas() {
        return repository.findByEstado("resuelta");
    }
    
}
