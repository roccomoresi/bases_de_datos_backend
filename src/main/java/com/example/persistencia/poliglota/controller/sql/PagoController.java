package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.PagoRequest;
import com.example.persistencia.poliglota.dto.PagoResponse;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.service.sql.PagoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sql/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // 🔹 Listar pagos por factura
    @GetMapping("/factura/{facturaId}")
    public List<Pago> getByFactura(@PathVariable Integer facturaId) {
        return pagoService.obtenerPagosPorFactura(facturaId);
    }

    // 🔹 Listar pagos por usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<Pago> getByUsuario(@PathVariable Integer usuarioId) {
        return pagoService.obtenerPagosPorUsuario(usuarioId);
    }

    // 🔹 Registrar nuevo pago
    @PostMapping
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest request) {
        try {
            Pago pagoGuardado = pagoService.registrarPago(
                    request.getIdFactura(),
                    request.getMonto(),
                    request.getMetodoPago()
            );

            // ✅ Devolvemos una respuesta simplificada (sin bucles infinitos)
            PagoResponse response = new PagoResponse(
                    pagoGuardado.getIdPago(),
                    pagoGuardado.getMetodoPago(),
                    pagoGuardado.getMontoPagado(),
                    pagoGuardado.getFactura().getIdFactura(),
                    pagoGuardado.getFactura().getEstado().name(),
                    pagoGuardado.getFactura().getDescripcionProceso()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al registrar el pago: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Error al registrar el pago", "detalle", e.getMessage())
            );
        }
    }
}
