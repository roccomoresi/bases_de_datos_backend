package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.service.mongo.HistorialEjecucionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/historial")
public class HistorialEjecucionController {

    private final HistorialEjecucionService service;

    public HistorialEjecucionController(HistorialEjecucionService service) {
        this.service = service;
    }

    // ───────────────────────────────────────────────
    // LISTAR / OBTENER
    // ───────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<HistorialEjecucion>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialEjecucion> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ───────────────────────────────────────────────
    // FILTROS
    // ───────────────────────────────────────────────

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HistorialEjecucion>> getByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(service.getByUsuario(usuarioId));
    }

    @GetMapping("/proceso/{procesoId}")
    public ResponseEntity<List<HistorialEjecucion>> getByProceso(@PathVariable String procesoId) {
        return ResponseEntity.ok(service.getByProceso(procesoId));
    }

    @GetMapping("/usuario/{usuarioId}/proceso/{procesoId}")
    public ResponseEntity<List<HistorialEjecucion>> getByUsuarioYProceso(
            @PathVariable Integer usuarioId,
            @PathVariable String procesoId) {
        return ResponseEntity.ok(service.getByUsuarioYProceso(usuarioId, procesoId));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<HistorialEjecucion>> getByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(service.getByRangoFechas(desde, hasta));
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<HistorialEjecucion>> getUltimos10() {
        return ResponseEntity.ok(service.getUltimos10());
    }

    // ───────────────────────────────────────────────
    // CREAR / ACTUALIZAR
    // ───────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<HistorialEjecucion> crear(@RequestBody HistorialEjecucion ejecucion) {
        return ResponseEntity.ok(service.save(ejecucion));
    }
}