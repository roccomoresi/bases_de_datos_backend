package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.dto.SensorDTO;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.service.cassandra.SensorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cassandra/sensores")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // 🔹 Listar todos los sensores (DTO completo)
    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensores() {
        List<SensorDTO> sensores = sensorService.getAll().stream()
                .map(SensorDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sensores);
    }

    // 🔹 Obtener un sensor por ID
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> obtenerSensorPorId(@PathVariable UUID id) {
        return sensorService.getById(id)
                .map(SensorDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear un nuevo sensor
    @PostMapping
    public ResponseEntity<SensorDTO> crearSensor(@RequestBody Sensor sensor) {
        if (sensor.getId() == null) {
            sensor.setId(UUID.randomUUID());
        }
        Sensor nuevo = sensorService.save(sensor);
        return ResponseEntity.ok(SensorDTO.fromEntity(nuevo));
    }

    // 🔹 Buscar sensores por ciudad
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<SensorDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(
                sensorService.buscarPorCiudad(ciudad).stream()
                        .map(SensorDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // 🔹 Buscar sensores por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SensorDTO>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                sensorService.buscarPorEstado(estado).stream()
                        .map(SensorDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // 🔹 Buscar sensores por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<SensorDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(
                sensorService.buscarPorTipo(tipo).stream()
                        .map(SensorDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // 🔹 Buscar sensores por nombre o código
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<SensorDTO>> obtenerPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(
                sensorService.buscarPorNombre(nombre).stream()
                        .map(SensorDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    // 🔹 Resumen de sensores (activos, inactivos, en falla)
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> obtenerResumenSensores() {
        List<Sensor> sensores = sensorService.getAll();

        long activos = sensores.stream().filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();
        long inactivos = sensores.stream().filter(s -> "inactivo".equalsIgnoreCase(s.getEstado())).count();
        long fallas = sensores.stream().filter(s -> "falla".equalsIgnoreCase(s.getEstado())).count();

        Map<String, Long> resumen = new HashMap<>();
        resumen.put("activos", activos);
        resumen.put("inactivos", inactivos);
        resumen.put("falla", fallas);

        return ResponseEntity.ok(resumen);
    }

        // 🔹 Buscar sensores por filtros combinados
    @GetMapping("/buscar")
    public ResponseEntity<List<SensorDTO>> buscarSensores(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ciudad
    ) {
        // 1️⃣ Obtenemos todos (en Cassandra esto puede ser optimizado luego con índices)
        List<Sensor> sensores = sensorService.getAll();

        // 2️⃣ Filtramos en backend (de forma combinada)
        List<SensorDTO> filtrados = sensores.stream()
                .filter(s -> tipo == null || s.getTipo() != null && s.getTipo().equalsIgnoreCase(tipo))
                .filter(s -> estado == null || s.getEstado() != null && s.getEstado().equalsIgnoreCase(estado))
                .filter(s -> nombre == null || s.getNombre() != null && s.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(s -> ciudad == null || s.getCiudad() != null && s.getCiudad().equalsIgnoreCase(ciudad))
                .map(SensorDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtrados);
    }

    // ✅ Obtener lista de países (sin repetir)
    @GetMapping("/paises")
    public ResponseEntity<List<String>> obtenerPaises() {
        List<String> paises = sensorService.getAll().stream()
                .map(Sensor::getPais)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return ResponseEntity.ok(paises);
    }

    // ✅ Obtener ciudades según país seleccionado
    @GetMapping("/ciudades")
    public ResponseEntity<List<String>> obtenerCiudadesPorPais(@RequestParam String pais) {
        List<String> ciudades = sensorService.getAll().stream()
                .filter(s -> pais.equalsIgnoreCase(s.getPais()))
                .map(Sensor::getCiudad)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return ResponseEntity.ok(ciudades);
    }

}
