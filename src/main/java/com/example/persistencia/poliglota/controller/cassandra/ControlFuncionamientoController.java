package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.ControlFuncionamiento;
import com.example.persistencia.poliglota.service.cassandra.ControlFuncionamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/funcionamiento")
public class ControlFuncionamientoController {

    private final ControlFuncionamientoService service;

    public ControlFuncionamientoController(ControlFuncionamientoService service) {
        this.service = service;
    }

    // ✅ Obtener el último control de un sensor
    @GetMapping("/{sensorId}")
    public ResponseEntity<?> obtenerUltimoControl(@PathVariable UUID sensorId) {
        return service.getUltimoControlPorSensor(sensorId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Obtener todos los controles históricos de un sensor
    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<ControlFuncionamiento>> obtenerControlesPorSensor(@PathVariable UUID sensorId) {
        List<ControlFuncionamiento> controles = service.getControlesPorSensor(sensorId);
        return controles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(controles);
    }

    // ✅ Crear un nuevo control
    @PostMapping
    public ResponseEntity<?> crearControl(@RequestBody ControlFuncionamiento control) {
        try {
            ControlFuncionamiento nuevo = service.guardarControl(control);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar control: " + e.getMessage());
        }
    }
}
