package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.FacturaRequest;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sql/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<?> crearFactura(
            @PathVariable Long usuarioId,
            @RequestBody FacturaRequest request) {
        try {
            Factura factura = facturaService.crearFactura(usuarioId, request.getMonto(), request.getFecha());
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }

    @GetMapping("/transacciones/facturas")
    public ResponseEntity<?> listarFacturas() {
        try {
            return ResponseEntity.ok(facturaService.obtenerTodas());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }
}
