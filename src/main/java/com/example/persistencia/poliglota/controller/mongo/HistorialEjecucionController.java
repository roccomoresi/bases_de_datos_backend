package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.service.mongo.HistorialEjecucionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/historial")
public class HistorialEjecucionController {

    private final HistorialEjecucionService service;

    public HistorialEjecucionController(HistorialEjecucionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HistorialEjecucion>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HistorialEjecucion>> getByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(service.getByUsuario(usuarioId));
    }

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<HistorialEjecucion>> getByProceso(@PathVariable String procesoId) {
        return ResponseEntity.ok(service.getByProceso(procesoId));
    }
}
