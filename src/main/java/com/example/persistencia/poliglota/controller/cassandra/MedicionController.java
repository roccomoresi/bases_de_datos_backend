package com.example.persistencia.poliglota.controller.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.service.cassandra.MedicionGeneratorService;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cassandra/mediciones")
public class MedicionController {

    private final MedicionService medicionService;
    private final MedicionGeneratorService generatorService;

    public MedicionController(MedicionService service, MedicionGeneratorService generatorService) {
    this.medicionService = service;
    this.generatorService = generatorService;
}

    @GetMapping("/{sensorId}")
    public List<Medicion> obtenerPorSensor(@PathVariable UUID sensorId) {
        return medicionService.obtenerPorSensor(sensorId);
    }

        @GetMapping("/ciudad/{ciudad}")
    public List<MedicionPorCiudad> obtenerPorCiudad(
            @PathVariable String ciudad,
            @RequestParam String pais) {
        return medicionService.obtenerPorCiudad(ciudad, pais);
    }

    @PostMapping
    public Medicion crearMedicion(@RequestBody Medicion medicion) {
        return medicionService.guardar(medicion);
    }

@PostMapping("/generar-una-vez")
public ResponseEntity<String> generarMedicionesUnaVez() {
    String resultado = generatorService.generarMedicionesUnaVez();
    return ResponseEntity.ok(resultado);
}


}
