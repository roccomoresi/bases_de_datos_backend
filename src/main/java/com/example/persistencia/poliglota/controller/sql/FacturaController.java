package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql/facturas")
public class FacturaController {

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    /* ───────────────────────────────────────────────
       📋 LISTAR FACTURAS
    ─────────────────────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<Factura>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Integer id) {
        Factura factura = service.getById(id);
        return factura != null ? ResponseEntity.ok(factura) : ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Factura>> getByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(service.getByUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Factura>> getByEstado(@PathVariable Factura.EstadoFactura estado) {
        return ResponseEntity.ok(service.getByEstado(estado));
    }

    /* ───────────────────────────────────────────────
       🧾 CREAR FACTURA (manual o automática)
    ─────────────────────────────────────────────── */
    @PostMapping
    public ResponseEntity<Factura> create(@RequestBody Factura factura) {
        return ResponseEntity.ok(service.save(factura));
    }

    /**
     * 📦 Crear factura desde proceso completado (Mongo)
     * Se usa cuando una solicitud en Mongo se completa y debe generar factura SQL.
     */
    @PostMapping("/generar")
    public ResponseEntity<Factura> generarFactura(
            @RequestParam Integer usuarioId,
            @RequestParam String procesosFacturados,
            @RequestParam Double monto
    ) {
        return ResponseEntity.ok(service.generarFactura(usuarioId, procesosFacturados, monto));
    }

    /* ───────────────────────────────────────────────
       💰 MARCAR FACTURA COMO PAGADA
    ─────────────────────────────────────────────── */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<Factura> marcarComoPagada(@PathVariable Integer id) {
        Factura factura = service.getById(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        service.marcarComoPagada(factura);
        return ResponseEntity.ok(factura);
    }

    /* ───────────────────────────────────────────────
       🧾 ACTUALIZAR FACTURA
    ─────────────────────────────────────────────── */
    @PutMapping("/{id}")
    public ResponseEntity<Factura> update(@PathVariable Integer id, @RequestBody Factura factura) {
        factura.setIdFactura(id);
        return ResponseEntity.ok(service.save(factura));
    }

    /* ───────────────────────────────────────────────
       ❌ ELIMINAR FACTURA
    ─────────────────────────────────────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
