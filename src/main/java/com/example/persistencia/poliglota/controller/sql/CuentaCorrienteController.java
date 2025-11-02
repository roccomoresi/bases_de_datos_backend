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

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(usuario);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/{idUsuario}/movimientos")
    public ResponseEntity<List<MovimientoCuenta>> obtenerMovimientos(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        CuentaCorriente cuenta = cuentaCorrienteService.obtenerPorUsuario(usuario);
        List<MovimientoCuenta> movimientos = movimientoCuentaService.obtenerPorCuenta(cuenta.getIdCuenta());
        return ResponseEntity.ok(movimientos);
    }
}
