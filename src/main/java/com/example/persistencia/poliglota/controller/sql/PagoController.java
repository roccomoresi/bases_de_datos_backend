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

    @GetMapping
    public List<Pago> getAll() {
        return pagoService.getAll();
    }

    @GetMapping("/factura/{facturaId}")
    public List<Pago> getByFactura(@PathVariable Integer facturaId) {
        return pagoService.getByFactura(facturaId);
    }

    // 🔥 Nuevo método usando tu DTO PagoRequest
    @PostMapping
    public ResponseEntity<Pago> registrarPago(@RequestBody PagoRequest request) {

        // Buscar la factura asociada
        Factura factura = facturaService.getById(request.getIdFactura());
        if (factura == null) {
            return ResponseEntity.badRequest().build();
        }

        // Convertir el String del método a enum (según tu modelo Pago)
        Pago.MetodoPago metodo = Pago.MetodoPago.valueOf(request.getMetodoPago().toLowerCase());

        // Registrar el pago y actualizar estado + saldo
        Pago pagoGuardado = pagoService.registrarPago(factura, request.getMonto(), metodo);

        return ResponseEntity.ok(pagoGuardado);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pagoService.delete(id);
    }
}
