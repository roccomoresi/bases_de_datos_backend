package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.FacturaCreateRequest;
import com.example.persistencia.poliglota.dto.FacturaResponse;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import com.example.persistencia.poliglota.service.sql.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finanzas/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final PagoService pagoService;
    private final UsuarioRepository usuarioRepository;
    private final FacturaRepository facturaRepository;

    /* ───────────────────────────────
       🔹 1. Listar facturas por usuario
    ─────────────────────────────── */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<FacturaResponse>> listarFacturas(@PathVariable Integer idUsuario) {
        List<Factura> facturas = facturaService.obtenerFacturasPorUsuario(idUsuario);

        List<FacturaResponse> resp = facturas.stream().map(this::mapFacturaToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    /* ───────────────────────────────
       🧾 2. Listar todas las facturas (para Admin)
    ─────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<FacturaResponse>> listarTodas() {
        List<Factura> facturas = facturaService.obtenerTodas();

        List<FacturaResponse> resp = facturas.stream().map(this::mapFacturaToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    /* ───────────────────────────────
       💰 3. Crear nueva factura (manual o desde admin)
    ─────────────────────────────── */
    @PostMapping
    public ResponseEntity<FacturaResponse> crearFactura(@RequestBody FacturaCreateRequest req) {
        if (req.getIdUsuario() == null) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setTotal(req.getTotal());
        factura.setDescripcionProceso(req.getDescripcionProceso());
        factura.setEstado(Factura.EstadoFactura.PENDIENTE);
        factura.setFechaEmision(java.time.LocalDateTime.now());

        Factura saved = facturaService.crearFactura(factura);
        FacturaResponse resp = mapFacturaToResponse(saved);

        return ResponseEntity.ok(resp);
    }

    /* ───────────────────────────────
       ✅ 4. Marcar factura como pagada
    ─────────────────────────────── */
    @PutMapping("/{idFactura}/pagar")
    public ResponseEntity<FacturaResponse> pagarFactura(@PathVariable Integer idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Registrar pago por el total con método MANUAL
        pagoService.registrarPago(idFactura, factura.getTotal(), "MANUAL");

        // Obtener factura actualizada
        Factura updated = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada tras pago"));

        FacturaResponse resp = mapFacturaToResponse(updated);
        return ResponseEntity.ok(resp);
    }

    /* ───────────────────────────────
       📊 5. Reportes globales de facturación (solo admin)
    ─────────────────────────────── */
    @GetMapping("/reportes")
    public ResponseEntity<Map<String, Object>> obtenerReportesGlobales() {
        var facturas = facturaRepository.findAll();

        var totalFacturado = facturas.stream()
                .mapToDouble(Factura::getTotal)
                .sum();

        var porProceso = facturas.stream()
                .collect(Collectors.groupingBy(
                        Factura::getDescripcionProceso,
                        Collectors.summingDouble(Factura::getTotal)
                ));

        var porEstado = facturas.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getEstado().name(),
                        Collectors.summingDouble(Factura::getTotal)
                ));

        var porUsuario = facturas.stream()
                .filter(f -> f.getUsuario() != null)
                .collect(Collectors.groupingBy(
                        f -> f.getUsuario().getNombreCompleto(),
                        Collectors.summingDouble(Factura::getTotal)
                ));

        Map<String, Object> reportes = Map.of(
                "totalFacturado", totalFacturado,
                "porProceso", porProceso,
                "porEstado", porEstado,
                "porUsuario", porUsuario
        );

        return ResponseEntity.ok(reportes);
    }

    /* ───────────────────────────────
       🔧 Método privado: mapea Factura → DTO
    ─────────────────────────────── */
 private FacturaResponse mapFacturaToResponse(Factura f) {
    var usuario = f.getUsuario();
    Pago pago = null;
    try {
        // Busca el primer pago asociado sin depender del fetch lazy
        var pagos = pagoService.obtenerPagosPorFactura(f.getIdFactura());
        if (pagos != null && !pagos.isEmpty()) {
            pago = pagos.get(0);
        }
    } catch (Exception e) {
        System.err.println("⚠️ No se pudo obtener pagos de factura " + f.getIdFactura() + ": " + e.getMessage());
    }

    return new FacturaResponse(
            f.getIdFactura(),
            usuario != null ? usuario.getIdUsuario() : null,
            usuario != null ? usuario.getNombreCompleto() : null,
            usuario != null ? usuario.getEmail() : null,
            f.getDescripcionProceso(),
            f.getEstado().name(),
            f.getTotal(),
            pago != null ? pago.getMetodoPago() : "—",
            pago != null ? pago.getFechaPago() : null,
            f.getFechaEmision()
    );
}

    }

