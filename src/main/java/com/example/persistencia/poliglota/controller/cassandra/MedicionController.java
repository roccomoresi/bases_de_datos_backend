package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.service.cassandra.MedicionGeneratorService;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/mediciones")
public class MedicionController {

    private final MedicionService medicionService;
    private final MedicionGeneratorService generatorService;

    public MedicionController(MedicionService medicionService, MedicionGeneratorService generatorService) {
        this.medicionService = medicionService;
        this.generatorService = generatorService;
    }

    // 🔹 Obtener mediciones por sensor
    @GetMapping("/{sensorId}")
    public List<Medicion> obtenerPorSensor(@PathVariable UUID sensorId) {
        return medicionService.obtenerPorSensor(sensorId);
    }

    @GetMapping("/{sensorId}/rango-fechas")
public ResponseEntity<?> obtenerPorSensorYRangoFechas(
        @PathVariable UUID sensorId,
        @RequestParam("desde") String desde,
        @RequestParam("hasta") String hasta) {
    try {
        List<Medicion> result = medicionService.obtenerPorSensorYRangoFechas(sensorId, desde, hasta);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body(result); // ✅ siempre JSON
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(
            Map.of(
                "error", "Error interno",
                "detalle", e.getMessage()
            )
        );
    }
}


    // 🔹 Obtener mediciones por ciudad y país
    @GetMapping("/ciudad/{ciudad}")
    public List<MedicionPorCiudad> obtenerPorCiudad(
            @PathVariable String ciudad,
            @RequestParam String pais) {
        return medicionService.obtenerPorCiudad(ciudad, pais);
    }

    // 🔹 Crear una medición (guardando en ambas tablas)
    @PostMapping
    public Medicion crearMedicion(@RequestBody Medicion medicion) {
        return medicionService.guardar(medicion);
    }

    // // 🔹 Generar mediciones de prueba (una sola vez)
    // @PostMapping("/generar-una-vez")
    // public ResponseEntity<String> generarMedicionesUnaVez() {
    //     String resultado = generatorService.generarMedicionesUnaVez();
    //     return ResponseEntity.ok(resultado);
    // }
}
