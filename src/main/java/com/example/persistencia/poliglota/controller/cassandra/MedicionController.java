package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/mediciones")
public class MedicionController {

    private final MedicionService medicionService;

    public MedicionController(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/{sensorId}")
    public List<Medicion> obtenerPorSensor(@PathVariable UUID sensorId) {
        return medicionService.obtenerPorSensor(sensorId);
    }

    @GetMapping("/{sensorId}/rango")
    public List<Medicion> obtenerPorRango(
            @PathVariable UUID sensorId,
            @RequestParam Instant desde,
            @RequestParam Instant hasta) {
        return medicionService.obtenerPorRango(sensorId, desde, hasta);
    }

    @PostMapping
    public Medicion crearMedicion(@RequestBody Medicion medicion) {
        return medicionService.guardar(medicion);
    }
}
