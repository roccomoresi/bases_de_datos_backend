package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/cuentas")
public class CuentaCorrienteController {

    private final CuentaCorrienteService service;

    public CuentaCorrienteController(CuentaCorrienteService service) {
        this.service = service;
    }

    /* ───────────────────────────────────────────────
       📋 LISTAR CUENTAS CORRIENTES
    ─────────────────────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<CuentaCorriente>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CuentaCorriente> getByUsuario(@PathVariable Integer usuarioId) {
        CuentaCorriente cuenta = service.getByUsuario(usuarioId);
        return cuenta != null ? ResponseEntity.ok(cuenta) : ResponseEntity.notFound().build();
    }

    @GetMapping("/movimientos/{usuarioId}")
public ResponseEntity<String> getMovimientos(@PathVariable Integer usuarioId) {
    CuentaCorriente cuenta = service.getByUsuario(usuarioId);
    return cuenta != null ? ResponseEntity.ok(cuenta.getHistorialMovimientos()) : ResponseEntity.notFound().build();
}


    /* ───────────────────────────────────────────────
       💵 AJUSTAR SALDO (ADMIN)
    ─────────────────────────────────────────────── */
    @PutMapping("/ajustar")
    public ResponseEntity<Void> ajustarSaldo(
            @RequestParam Integer usuarioId,
            @RequestParam Double monto,
            @RequestParam String motivo
    ) {
        service.ajustarSaldo(usuarioId, monto, motivo);
        return ResponseEntity.ok().build();
    }

    /* ───────────────────────────────────────────────
       🗑️ ELIMINAR CUENTA CORRIENTE
    ─────────────────────────────────────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<Void> deleteByUsuario(@PathVariable Integer usuarioId) {
        service.deleteByUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
