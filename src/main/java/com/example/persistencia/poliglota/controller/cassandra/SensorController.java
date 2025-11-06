package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.service.cassandra.SensorService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/sensores")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    // 🔹 Listar todos los sensores
    @GetMapping
    public List<Sensor> getAllSensores() {
        return sensorService.getAll();
    }

    // 🔹 Crear un nuevo sensor
    @PostMapping
    public Sensor crearSensor(@RequestBody Sensor sensor) {
        if (sensor.getId() == null) {
            sensor.setId(UUID.randomUUID());
        }
        return sensorService.save(sensor);
    }

    // 🔹 Buscar sensores por ciudad
    @GetMapping("/ciudad/{ciudad}")
    public List<Sensor> obtenerPorCiudad(@PathVariable String ciudad) {
        return sensorService.buscarPorCiudad(ciudad);
    }

    // 🔹 Buscar sensores por estado
    @GetMapping("/estado/{estado}")
    public List<Sensor> obtenerPorEstado(@PathVariable String estado) {
        return sensorService.buscarPorEstado(estado);
    }

    @GetMapping("/resumen")
public Map<String, Long> obtenerResumenSensores() {
    List<Sensor> sensores = sensorService.getAll();

    long activos = sensores.stream().filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();
    long inactivos = sensores.stream().filter(s -> "inactivo".equalsIgnoreCase(s.getEstado())).count();
    long fallas = sensores.stream().filter(s -> "falla".equalsIgnoreCase(s.getEstado())).count();

    Map<String, Long> resumen = new HashMap<>();
    resumen.put("activos", activos);
    resumen.put("inactivos", inactivos);
    resumen.put("falla", fallas);

    return resumen;
}

}
