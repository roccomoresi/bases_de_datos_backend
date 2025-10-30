package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.model.sql.Pago.MetodoPago;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import com.example.persistencia.poliglota.service.sql.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final FacturaService facturaService;

    public PagoController(PagoService pagoService, FacturaService facturaService) {
        this.pagoService = pagoService;
        this.facturaService = facturaService;
    }

    /* ───────────────────────────────────────────────
       📋 LISTAR PAGOS
    ─────────────────────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<Pago>> getAll() {
        return ResponseEntity.ok(pagoService.getAll());
    }

    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<Pago>> getByFactura(@PathVariable Integer facturaId) {
        return ResponseEntity.ok(pagoService.getByFactura(facturaId));
    }

    /* ───────────────────────────────────────────────
       💰 REGISTRAR NUEVO PAGO
    ─────────────────────────────────────────────── */
    @PostMapping("/registrar")
    public ResponseEntity<Pago> registrarPago(
            @RequestParam Integer facturaId,
            @RequestParam Double monto,
            @RequestParam(defaultValue = "efectivo") MetodoPago metodo
    ) {
        Factura factura = facturaService.getById(facturaId);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }

        Pago nuevoPago = pagoService.registrarPago(factura, monto, metodo);
        return ResponseEntity.ok(nuevoPago);
    }

    /* ───────────────────────────────────────────────
       🗑️ ELIMINAR PAGO
    ─────────────────────────────────────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
