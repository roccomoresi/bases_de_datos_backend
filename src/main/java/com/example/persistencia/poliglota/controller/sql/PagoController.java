package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.PagoRequest;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import com.example.persistencia.poliglota.service.sql.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 🔹 Listar todos los pagos
    @GetMapping
    public List<Pago> getAll() {
        return pagoService.getAll();
    }

    // 🔹 Listar pagos por factura
    @GetMapping("/factura/{facturaId}")
    public List<Pago> getByFactura(@PathVariable Integer facturaId) {
        return pagoService.getByFactura(facturaId);
    }

    // 🔹 Registrar nuevo pago
    @PostMapping
    public ResponseEntity<Pago> registrarPago(@RequestBody PagoRequest request) {
        try {
            // Buscar la factura asociada
            Factura factura = facturaService.getById(request.getIdFactura())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

            // Registrar el pago (usa el método de PagoService que ya tenés)
            Pago pagoGuardado = pagoService.registrarPago(
                    factura.getIdFactura(),
                    request.getMonto(),
                    request.getMetodoPago()
            );

            return ResponseEntity.ok(pagoGuardado);
        } catch (Exception e) {
            log.error("❌ Error al registrar el pago: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 🔹 Eliminar un pago
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pagoService.delete(id);
    }
}
