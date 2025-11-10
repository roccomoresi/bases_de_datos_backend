package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AlertaService {

    private final AlertaRepository repository;
    private final SensorRepository sensorRepository;
    private final MongoTemplate mongoTemplate;

    public AlertaService(AlertaRepository repository,
                         SensorRepository sensorRepository,
                         MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.sensorRepository = sensorRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // ---------------------------------------------------------------------
    // 🔎 FILTRO DINÁMICO REAL EN MONGO
    // ---------------------------------------------------------------------
    public List<Alerta> filtrarAlertas(String tipo, String severidad, String estado,
                                       String ciudad, String pais, UUID sensorId) {

        Query query = new Query();
        List<Criteria> criterios = new ArrayList<>();

        if (tipo != null && !tipo.isBlank()) criterios.add(Criteria.where("tipo").is(tipo));
        if (severidad != null && !severidad.isBlank()) criterios.add(Criteria.where("severidad").is(severidad));
        if (estado != null && !estado.isBlank()) criterios.add(Criteria.where("estado").is(estado));
        if (ciudad != null && !ciudad.isBlank()) criterios.add(Criteria.where("ciudad").is(ciudad));
        if (pais != null && !pais.isBlank()) criterios.add(Criteria.where("pais").is(pais));
        if (sensorId != null) criterios.add(Criteria.where("sensorId").is(sensorId));

        if (!criterios.isEmpty())
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));

        // 🔽 Ordenamos las alertas por fecha descendente (más recientes primero)
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha"));

        return mongoTemplate.find(query, Alerta.class);
    }

    // ---------------------------------------------------------------------
    // 🌡️ CREACIÓN SIMPLE
    // ---------------------------------------------------------------------
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

    // ---------------------------------------------------------------------
    // 👷 ASIGNACIÓN DE TÉCNICO
    // ---------------------------------------------------------------------
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

    public List<Alerta> listarResueltas() {
        return repository.findByEstado("resuelta");
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
}
