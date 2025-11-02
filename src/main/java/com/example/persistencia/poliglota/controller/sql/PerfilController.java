package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.service.sql.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sql/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final FacturaService facturaService;
    private final PagoService pagoService;

    public PerfilController(UsuarioService usuarioService,
                            CuentaCorrienteService cuentaCorrienteService,
                            FacturaService facturaService,
                            PagoService pagoService) {
        this.usuarioService = usuarioService;
        this.cuentaCorrienteService = cuentaCorrienteService;
        this.facturaService = facturaService;
        this.pagoService = pagoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> getPerfil(@PathVariable Integer usuarioId) {
        // 🧍 Usuario
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 💰 Cuenta corriente
        // 💰 Cuenta corriente
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(usuario);


        // 🧾 Facturas y pagos
        List<Factura> facturas = facturaService.obtenerFacturasPorUsuario(usuarioId);
        List<Pago> pagos = pagoService.obtenerPagosPorUsuario(usuarioId); // 👈 lo agregamos abajo

        // 📊 Calcular métricas
        double totalPagado = pagos.stream()
                .mapToDouble(Pago::getMontoPagado)
                .sum();

        long facturasPendientes = facturas.stream()
                .filter(f -> f.getEstado().name().equals("PENDIENTE"))
                .count();

        long facturasPagadas = facturas.stream()
                .filter(f -> f.getEstado().name().equals("PAGADA"))
                .count();

        return ResponseEntity.ok(Map.of(
                "usuario", usuario,
                "cuentaCorriente", cuenta,
                "resumen", Map.of(
                        "totalPagado", totalPagado,
                        "facturasPendientes", facturasPendientes,
                        "facturasPagadas", facturasPagadas
                )
        ));
    }
}
