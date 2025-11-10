package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.service.mongo.HistorialEjecucionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mongo/historial")
public class HistorialEjecucionController {

    private final HistorialEjecucionService service;

    public HistorialEjecucionController(HistorialEjecucionService service) {
        this.service = service;
    }

    // ============================================================
    // LISTAR / OBTENER
    // ============================================================

    @GetMapping
    public ResponseEntity<List<HistorialEjecucion>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialEjecucion> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ============================================================
    // FILTROS
    // ============================================================

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
            @PathVariable String procesoId
    ) {
        return ResponseEntity.ok(service.getByUsuarioYProceso(usuarioId, procesoId));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<HistorialEjecucion>> getByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        return ResponseEntity.ok(service.getByRangoFechas(desde, hasta));
    }

    @GetMapping("/usuario/{usuarioId}/top5")
    public ResponseEntity<List<HistorialEjecucion>> getTop5ByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(service.getTop5ByUsuario(usuarioId));
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<HistorialEjecucion>> getUltimos10() {
        return ResponseEntity.ok(service.getUltimos10());
    }

    // ============================================================
    // CREAR / ACTUALIZAR
    // ============================================================

    @PostMapping
    public ResponseEntity<HistorialEjecucion> crear(@RequestBody HistorialEjecucion ejecucion) {
        return ResponseEntity.ok(service.save(ejecucion));
    }

    // ============================================================
    // VALIDACIÓN (moderación interna)
    // ============================================================

    @GetMapping("/pendientes-validacion")
    public ResponseEntity<List<HistorialEjecucion>> getPendientesValidacion() {
        return ResponseEntity.ok(service.getPendientesValidacion());
    }

    @GetMapping("/validados")
    public ResponseEntity<List<HistorialEjecucion>> getValidados() {
        return ResponseEntity.ok(service.getValidados());
    }

    @PutMapping("/{id}/validar")
    public ResponseEntity<HistorialEjecucion> validar(
            @PathVariable String id,
            @RequestParam String validador,
            @RequestParam(required = false) String observaciones
    ) {
        return ResponseEntity.ok(service.validar(id, validador, observaciones));
    }

    @PutMapping("/{id}/desvalidar")
    public ResponseEntity<HistorialEjecucion> desvalidar(@PathVariable String id) {
        return ResponseEntity.ok(service.desvalidar(id));
    }

    // ============================================================
    // *NUEVO*: ENDPOINTS “SOLO VALIDADOS” (para el usuario final)
    // ============================================================

    // Lista solo validados del usuario
    @GetMapping("/usuario/{usuarioId}/validados")
    public ResponseEntity<List<HistorialEjecucion>> getValidadosByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(service.getValidadosByUsuario(usuarioId));
    }

    // Lista solo validados del usuario filtrando por proceso
    @GetMapping("/usuario/{usuarioId}/proceso/{procesoId}/validados")
    public ResponseEntity<List<HistorialEjecucion>> getValidadosByUsuarioYProceso(
            @PathVariable Integer usuarioId,
            @PathVariable String procesoId
    ) {
        return ResponseEntity.ok(service.getValidadosByUsuarioYProceso(usuarioId, procesoId));
    }
}