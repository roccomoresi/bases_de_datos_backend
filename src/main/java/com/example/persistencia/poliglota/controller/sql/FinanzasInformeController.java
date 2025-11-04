package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.repository.sql.CuentaCorrienteRepository;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/finanzas/informes")
public class FinanzasInformeController {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;

    public FinanzasInformeController(FacturaRepository facturaRepository,
                                     PagoRepository pagoRepository,
                                     CuentaCorrienteRepository cuentaCorrienteRepository) {
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumenGlobal() {
        var facturas = facturaRepository.findAll();
        long totalFacturas = facturas.size();
        long totalFacturasPagadas = facturas.stream()
                .filter(f -> f.getEstado() != null && "PAGADA".equals(f.getEstado().name()))
                .count();
        double montoFacturado = facturas.stream()
                .map(Factura::getTotal)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();
        var pagos = pagoRepository.findAll();
        double montoPagado = pagos.stream()
                .map(p -> p.getMontoPagado())
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();
        double saldoGeneral = cuentaCorrienteRepository.findAll().stream()
                .map(c -> c.getSaldo())
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        return ResponseEntity.ok(Map.of(
                "totalFacturas", totalFacturas,
                "totalFacturasPagadas", totalFacturasPagadas,
                "montoFacturado", montoFacturado,
                "montoPagado", montoPagado,
                "saldoGeneral", saldoGeneral
        ));
    }
}

