package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.FacturaCreateRequest;
import com.example.persistencia.poliglota.dto.FacturaResponse;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import com.example.persistencia.poliglota.service.sql.FacturaService;
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
    private final UsuarioRepository usuarioRepository;

    // 🔹 1. Listar facturas por usuario
    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<FacturaResponse>> listarFacturas(@PathVariable Integer idUsuario) {
        List<Factura> facturas = facturaService.obtenerFacturasPorUsuario(idUsuario);
        List<FacturaResponse> resp = facturas.stream().map(f -> new FacturaResponse(
                f.getIdFactura(),
                f.getUsuario() != null ? f.getUsuario().getIdUsuario() : null,
                f.getFechaEmision(),
                f.getEstado().name(),
                f.getTotal(),
                f.getDescripcionProceso()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    // 🔹 2. Crear nueva factura
    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody FacturaCreateRequest req) {
        if (req.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo idUsuario es obligatorio"));
        }
    
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(req.getIdUsuario()); // ✅ solo referencia persistente
    
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setTotal(req.getTotal());
        factura.setDescripcionProceso(req.getDescripcionProceso());
        factura.setEstado(Factura.EstadoFactura.PENDIENTE);
        factura.setFechaEmision(java.time.LocalDateTime.now());
    
        Factura saved = facturaService.crearFactura(factura);
    
        FacturaResponse resp = new FacturaResponse(
                saved.getIdFactura(),
                saved.getUsuario().getIdUsuario(),
                saved.getFechaEmision(),
                saved.getEstado().name(),
                saved.getTotal(),
                saved.getDescripcionProceso()
        );
    
        return ResponseEntity.ok(resp);
    }
    

    // 🔹 3. Marcar factura como pagada
    @PutMapping("/{idFactura}/pagar")
    public ResponseEntity<?> pagarFactura(@PathVariable Integer idFactura) {
        Factura updated = facturaService.marcarComoPagada(idFactura);

        FacturaResponse resp = new FacturaResponse(
                updated.getIdFactura(),
                updated.getUsuario() != null ? updated.getUsuario().getIdUsuario() : null,
                updated.getFechaEmision(),
                updated.getEstado().name(),
                updated.getTotal(),
                updated.getDescripcionProceso()
        );

        return ResponseEntity.ok(resp);
    }
}
