package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para la gestión de alertas (MongoDB).
 * Permite crear, listar, resolver y buscar alertas activas o por ubicación.
 */
@RestController
@RequestMapping("/api/mongo/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    // -----------------------------------------------------------------------
    // 🔍 LISTAR TODAS LAS ALERTAS
    // -----------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Alerta>> getAll() {
        return ResponseEntity.ok(service.listar());
    }

    // -----------------------------------------------------------------------
    // 🟢 LISTAR SOLO LAS ALERTAS ACTIVAS
    // -----------------------------------------------------------------------
    @GetMapping("/activas")
    public ResponseEntity<List<Alerta>> getActivas() {
        return ResponseEntity.ok(service.listarActivas());
    }

    // -----------------------------------------------------------------------
    // 🔵 LISTAR SOLO LAS ALERTAS RESUELTAS
    // -----------------------------------------------------------------------
    @GetMapping("/resueltas")
    public ResponseEntity<List<Alerta>> getResueltas() {
        return ResponseEntity.ok(service.listarResueltas());
    }

    // -----------------------------------------------------------------------
    // 🌍 BUSCAR ALERTAS POR CIUDAD Y PAÍS
    // -----------------------------------------------------------------------
    @GetMapping("/ubicacion")
    public ResponseEntity<List<Alerta>> getPorUbicacion(
            @RequestParam String ciudad,
            @RequestParam String pais
    ) {
        List<Alerta> alertas = service.buscarPorUbicacion(ciudad, pais);
        if (alertas.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(alertas);
    }

    // -----------------------------------------------------------------------
    // 🚨 CREAR ALERTA MANUAL O POR BACKEND
    // -----------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String tipo = (String) body.getOrDefault("tipo", "climatica");
            String descripcion = (String) body.getOrDefault("descripcion", "Alerta generada");
            String ciudad = (String) body.getOrDefault("ciudad", "Desconocida");
            String pais = (String) body.getOrDefault("pais", "Desconocido");
            UUID sensorId = body.containsKey("sensorId")
                    ? UUID.fromString((String) body.get("sensorId"))
                    : null;
            Map<String, Object> detalles = (Map<String, Object>) body.getOrDefault("detalles", Map.of());

            Alerta alerta = service.crearConDetalles(sensorId, tipo, descripcion, ciudad, pais, detalles);
            return ResponseEntity.status(HttpStatus.CREATED).body(alerta);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al crear la alerta", "detalle", e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // 👷‍♂️ ASIGNAR TÉCNICO MANUALMENTE
    // -----------------------------------------------------------------------
    @PutMapping("/{id}/asignar-manual")
    public ResponseEntity<?> asignarTecnicoManual(
            @PathVariable UUID id,
            @RequestParam Integer tecnicoId,
            @RequestParam String nombreTecnico
    ) {
        try {
            Alerta alertaActualizada = service.asignarTecnico(id, tecnicoId, nombreTecnico);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Técnico asignado manualmente",
                    "tecnicoAsignado", nombreTecnico,
                    "alerta", alertaActualizada
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al asignar técnico manualmente", "detalle", e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // 🧩 RESOLVER ALERTA POR ID
    // -----------------------------------------------------------------------
    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolver(@PathVariable UUID id) {
        try {
            Alerta alerta = service.resolver(id);
            return ResponseEntity.ok(alerta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se pudo resolver la alerta", "detalle", e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // 🗑️ ELIMINAR ALERTA POR ID
    // -----------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Alerta eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró la alerta con el ID especificado"));
        }
    }

    // -----------------------------------------------------------------------
    // 🎯 FILTRAR ALERTAS DINÁMICAMENTE
    // -----------------------------------------------------------------------
    @GetMapping("/filtrar")
    public ResponseEntity<List<Alerta>> filtrarAlertas(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String severidad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) UUID sensorId
    ) {
        List<Alerta> alertas = service.filtrarAlertas(tipo, severidad, estado, ciudad, pais, sensorId);
        if (alertas.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(alertas);
    }

    // -----------------------------------------------------------------------
    // 🌐 ALERTAS GLOBALES (Mongo + Cassandra)
    // -----------------------------------------------------------------------
    @GetMapping("/global")
    public ResponseEntity<?> getAlertasGlobales() {
        try {
            List<Map<String, Object>> resultado = new ArrayList<>();
            List<Alerta> alertas = service.listar();

            for (Alerta alerta : alertas) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("sensorId", alerta.getSensorId());
                item.put("ciudad", alerta.getCiudad());
                item.put("pais", alerta.getPais());
                item.put("descripcion", alerta.getDescripcion());
                item.put("severidad", alerta.getSeveridad());
                item.put("fechaAlerta", alerta.getFecha());
                item.put("fuente", alerta.getFuente());
                item.put("estado", alerta.getEstado());

                try {
                    var restTemplate = new org.springframework.web.client.RestTemplate();
                    var url = "http://localhost:8080/api/cassandra/mediciones/sensor/" + alerta.getSensorId();
                    var response = restTemplate.getForEntity(url, List.class);

                    if (response.getStatusCode().is2xxSuccessful()
                            && response.getBody() != null
                            && !response.getBody().isEmpty()) {
                        List<?> mediciones = response.getBody();
                        item.put("ultimaMedicion", mediciones.get(mediciones.size() - 1));
                    } else {
                        item.put("ultimaMedicion", "No disponible");
                    }
                } catch (Exception e) {
                    item.put("ultimaMedicion", "No disponible");
                }

                resultado.add(item);
            }

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudieron combinar las alertas con las mediciones", "detalle", e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // 🤖 ASIGNAR AUTOMÁTICAMENTE UN TÉCNICO (SQL)
    // -----------------------------------------------------------------------
    @PutMapping("/{id}/asignar-auto")
    public ResponseEntity<?> asignarTecnicoAutomatico(@PathVariable UUID id) {
        try {
            var restTemplate = new org.springframework.web.client.RestTemplate();
            var response = restTemplate.getForEntity(
                    "http://localhost:8080/api/sql/usuarios/tecnicos", List.class
            );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontraron técnicos disponibles"));
            }

            List<Map<String, Object>> listaTecnicos = (List<Map<String, Object>>) response.getBody();
            Random random = new Random();
            Map<String, Object> tecnicoMap = listaTecnicos.get(random.nextInt(listaTecnicos.size()));

            Object rawId = tecnicoMap.get("idUsuario");
            Integer tecnicoId = rawId instanceof Integer ? (Integer) rawId :
                    rawId instanceof Long ? ((Long) rawId).intValue() :
                    rawId instanceof String ? Integer.parseInt((String) rawId) : null;

            String nombreTecnico = String.valueOf(tecnicoMap.get("nombreCompleto"));
            if (tecnicoId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo determinar el ID del técnico"));
            }

            Alerta alertaActualizada = service.asignarTecnico(id, tecnicoId, nombreTecnico);

            System.out.printf("👷 Asignado automáticamente el técnico %s (ID=%d) a la alerta %s%n",
                    nombreTecnico, tecnicoId, id);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Alerta asignada automáticamente al técnico",
                    "tecnicoAsignado", nombreTecnico,
                    "alerta", alertaActualizada
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al asignar técnico automáticamente", "detalle", e.getMessage()));
        }
    }
}
