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

    @PostMapping
    public ResponseEntity<SolicitudProceso> create(
            @RequestParam UUID usuarioId,
            @RequestParam UUID procesoId
    ) {
        return ResponseEntity.ok(service.create(usuarioId, procesoId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudProceso> updateEstado(
            @PathVariable UUID id,
            @RequestParam String nuevoEstado
    ) {
        return ResponseEntity.ok(service.updateEstado(id, nuevoEstado));
    }

    @PutMapping("/{id}/resultado")
    public ResponseEntity<SolicitudProceso> agregarResultado(
            @PathVariable UUID id,
            @RequestParam String resultado
    ) {
        return ResponseEntity.ok(service.agregarResultado(id, resultado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
