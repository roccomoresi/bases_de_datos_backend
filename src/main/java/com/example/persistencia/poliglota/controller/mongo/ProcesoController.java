package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/procesos")
public class ProcesoController {

    private final ProcesoService service;

    public ProcesoController(ProcesoService service) {
        this.service = service;
    }

    // =========================================================
    // V1 - Endpoints estándar (sin mapper)
    // =========================================================

    // LISTAR / BUSCAR
    @GetMapping
    public ResponseEntity<List<Proceso>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Proceso>> getActivos() {
        return ResponseEntity.ok(service.getActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proceso> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Proceso>> getByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(service.getByTipo(tipo));
    }

    // CREAR / ACTUALIZAR / ELIMINAR
    @PostMapping
    public ResponseEntity<Proceso> save(@RequestBody Proceso proceso) {
        return ResponseEntity.ok(service.save(proceso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proceso> update(@PathVariable String id, @RequestBody Proceso proceso) {
        return ResponseEntity.ok(service.update(id, proceso));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Proceso> toggleEstado(@PathVariable String id) {
        return ResponseEntity.ok(service.toggleEstado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // NUEVO: EJECUCIONES (manual y automática)
    // =========================================================

    // Ejecuta automáticamente (por ejemplo al pagar)
    @PostMapping("/{id}/auto")
    public ResponseEntity<Void> ejecutarAuto(@PathVariable String id) {
        service.ejecutarProceso(id);
        return ResponseEntity.ok().build();
    }

    // Ejecuta manualmente (desde Swagger o interfaz)
    @PostMapping("/{id}/ejecutar")
    public ResponseEntity<HistorialEjecucion> ejecutarManual(
            @PathVariable String id,
            @RequestParam Integer usuarioId
    ) {
        return ResponseEntity.ok(service.ejecutarManual(id, usuarioId));
    }
}