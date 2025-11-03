package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.service.sql.MovimientoCuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/finanzas/movimientos")
@RequiredArgsConstructor
public class MovimientoCuentaController {

    private final MovimientoCuentaService movimientoCuentaService;

    /**
     * 🔹 1. Listar todos los movimientos
     */
    @GetMapping
    public ResponseEntity<List<MovimientoCuenta>> listarTodos() {
        log.info("📄 Listando todos los movimientos de cuenta");
        List<MovimientoCuenta> movimientos = movimientoCuentaService.getAll();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * 🔹 2. Listar movimientos por cuenta corriente
     */
    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<List<MovimientoCuenta>> listarPorCuenta(@PathVariable Integer idCuenta) {
        log.info("📄 Listando movimientos de la cuenta ID {}", idCuenta);
        List<MovimientoCuenta> movimientos = movimientoCuentaService.getByCuenta(idCuenta);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * 🔹 3. Registrar un movimiento manual (opcional, para testing)
     */
    @PostMapping
    public ResponseEntity<MovimientoCuenta> crearMovimiento(@RequestBody MovimientoCuenta movimiento) {
        log.info("💰 Creando movimiento manual para cuenta {}", 
                movimiento.getCuentaCorriente() != null ? movimiento.getCuentaCorriente().getIdCuenta() : "sin cuenta");
        MovimientoCuenta saved = movimientoCuentaService.save(movimiento);
        return ResponseEntity.ok(saved);
    }

    /**
     * 🔹 4. Eliminar un movimiento (solo para pruebas)
     */
    @DeleteMapping("/{idMovimiento}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Integer idMovimiento) {
        log.info("🗑 Eliminando movimiento ID {}", idMovimiento);
        movimientoCuentaService.delete(idMovimiento);
        return ResponseEntity.noContent().build();
    }
}
