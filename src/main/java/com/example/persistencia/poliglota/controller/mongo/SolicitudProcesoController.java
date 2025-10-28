package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.service.mongo.SolicitudProcesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/solicitudes")
public class SolicitudProcesoController {

    private final SolicitudProcesoService service;

    public SolicitudProcesoController(SolicitudProcesoService service) {
        this.service = service;
    }

    /* ───────────────────────────────────────────────
       🔹 LISTAR SOLICITUDES
    ─────────────────────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<SolicitudProceso>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudProceso> getById(@PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitudProceso>> getByUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.getByUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudProceso>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.getByEstado(estado));
    }

    /* ───────────────────────────────────────────────
       🟢 CREAR NUEVA SOLICITUD
    ─────────────────────────────────────────────── */
    @PostMapping
    public ResponseEntity<SolicitudProceso> create(
            @RequestParam Integer usuarioId,
            @RequestParam UUID procesoId
    ) {
        return ResponseEntity.ok(service.create(usuarioId, procesoId));
    }

    /* ───────────────────────────────────────────────
       🔄 ACTUALIZAR ESTADO
    ─────────────────────────────────────────────── */
    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudProceso> updateEstado(
            @PathVariable UUID id,
            @RequestParam String estado
    ) {
        return ResponseEntity.ok(service.updateEstado(id, estado));
    }

    /* ───────────────────────────────────────────────
       📝 AGREGAR RESULTADO
    ─────────────────────────────────────────────── */
    @PutMapping("/{id}/resultado")
    public ResponseEntity<SolicitudProceso> updateResultado(
            @PathVariable UUID id,
            @RequestParam String resultado
    ) {
        return ResponseEntity.ok(service.updateResultado(id, resultado));
    }

    /* ───────────────────────────────────────────────
       ❌ ELIMINAR SOLICITUD
    ─────────────────────────────────────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
