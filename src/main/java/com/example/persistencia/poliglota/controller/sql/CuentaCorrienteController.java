package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import com.example.persistencia.poliglota.service.sql.MovimientoCuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finanzas/cuenta")
@RequiredArgsConstructor
public class CuentaCorrienteController {

    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;
    private final UsuarioRepository usuarioRepository;

    // 🔹 Obtener la cuenta corriente de un usuario (crea si no existe)
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(usuario);
        return ResponseEntity.ok(cuenta);
    }

    // 🔹 Obtener movimientos de la cuenta corriente de un usuario
    @GetMapping("/{idUsuario}/movimientos")
    public ResponseEntity<List<MovimientoCuenta>> obtenerMovimientos(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        CuentaCorriente cuenta = cuentaCorrienteService.obtenerPorUsuario(usuario);
        List<MovimientoCuenta> movimientos = movimientoCuentaService.obtenerPorCuenta(cuenta.getIdCuenta());
        return ResponseEntity.ok(movimientos);
    }

    // 🔹 (ADMIN) Listar todas las cuentas corrientes
    @GetMapping
    public ResponseEntity<List<CuentaCorriente>> listarTodas() {
        List<CuentaCorriente> cuentas = cuentaCorrienteService.obtenerTodas();
        return ResponseEntity.ok(cuentas);
    }

    // 🔹 (ADMIN) Obtener movimientos de una cuenta específica por su ID
    @GetMapping("/movimientos/{idCuenta}")
    public ResponseEntity<List<MovimientoCuenta>> obtenerMovimientosPorCuenta(@PathVariable Integer idCuenta) {
        List<MovimientoCuenta> movimientos = movimientoCuentaService.obtenerPorCuenta(idCuenta);
        return ResponseEntity.ok(movimientos);
    }

    // 🔹 (ADMIN) Ajustar manualmente el saldo de una cuenta corriente
    @PostMapping("/{idCuenta}/ajustar")
    public ResponseEntity<CuentaCorriente> ajustarSaldo(
            @PathVariable Integer idCuenta,
            @RequestParam Double monto,
            @RequestParam(defaultValue = "true") boolean esCredito,
            @RequestParam(defaultValue = "Ajuste manual de saldo") String descripcion
    ) {
        CuentaCorriente cuenta = cuentaCorrienteService.obtenerPorId(idCuenta);

        // Registrar el movimiento (crédito o débito)
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                descripcion,
                monto,
                esCredito ? MovimientoCuenta.TipoMovimiento.CREDITO : MovimientoCuenta.TipoMovimiento.DEBITO
        );

        // Actualizar el saldo
        cuentaCorrienteService.actualizarSaldo(cuenta, monto, esCredito);

        return ResponseEntity.ok(cuenta);
    }
}
