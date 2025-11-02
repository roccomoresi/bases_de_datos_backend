package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.PagoRequest;
import com.example.persistencia.poliglota.dto.PagoResponse;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.service.sql.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/finanzas/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest request) {
        log.info("💳 Registrando pago de factura ID {} por ${} ({})",
                request.getIdFactura(), request.getMonto(), request.getMetodoPago());

        if (request.getIdFactura() == null || request.getMonto() == null || request.getMetodoPago() == null) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Campos requeridos: idFactura, monto, metodoPago", "Faltan campos obligatorios"));
        }

        try {
            Pago pago = pagoService.registrarPago(
                    request.getIdFactura(),
                    request.getMonto(),
                    request.getMetodoPago()
            );

            PagoResponse resp = new PagoResponse(
                    pago.getIdPago(),
                    pago.getFactura().getIdFactura(),
                    pago.getFactura().getUsuario().getIdUsuario(),
                    pago.getMetodoPago(),
                    pago.getMontoPagado(),
                    pago.getFechaPago().toString()
            );

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("❌ Error registrando pago: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("Error interno", e.getMessage()));
        }
    }

    record ErrorResponse(String error, String detalle) {}
}
