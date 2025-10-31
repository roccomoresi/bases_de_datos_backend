package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.*;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/mediciones")
public class MedicionController {

    private final MedicionService medicionService;

    public MedicionController(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/pais/{pais}")
public List<MedicionPorPais> obtenerPorPais(@PathVariable String pais) {
    return medicionService.obtenerPorPais(pais);
}


    // 🔹 Obtener todas las mediciones por sensor
    @GetMapping("/{sensorId}")
    public ResponseEntity<?> obtenerPorSensor(@PathVariable UUID sensorId) {
        try {
            List<MedicionPorSensor> result = medicionService.obtenerPorSensor(sensorId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Error interno", "detalle", e.getMessage())
            );
        }
    }

    // 🔹 Obtener mediciones por sensor y rango de fechas
    @GetMapping("/{sensorId}/rango-fechas")
    public ResponseEntity<?> obtenerPorSensorYRangoFechas(
            @PathVariable UUID sensorId,
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta) {
        try {
            List<MedicionPorSensor> result = medicionService.obtenerPorSensorYRangoFechas(sensorId, desde, hasta);
            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error interno", "detalle", e.getMessage())
            );
        }
    }





    // 🔹 Obtener mediciones de rango global (usa los buckets)
    @GetMapping("/rango-global")
    public ResponseEntity<?> getMedicionesRangoGlobal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        try {
            List<MedicionPorRangoGlobal> result = medicionService.obtenerMedicionesRangoGlobal(desde, hasta);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error interno", "detalle", e.getMessage())
            );
        }
    }

    // 🔹 Crear una nueva medición (se guarda en las 4 tablas)
    @PostMapping
public ResponseEntity<?> crearMedicion(@RequestBody Medicion medicion) {
    try {
        Medicion result = medicionService.guardar(medicion);
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(
            Map.of(
                "error", "Error al crear medición",
                "detalle", e.getMessage()
            )
        );
    }
}

}
