package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.service.sql.TransaccionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql/transacciones") // ✅ Cambiado para evitar conflicto con /facturas
@CrossOrigin
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // ✅ Crear usuario
    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return transaccionService.crearUsuario(usuario.getNombre(), usuario.getEmail());
    }

    // ✅ Crear factura desde JSON
    @PostMapping("/facturas/{usuarioId}")
    public Factura crearFactura(@PathVariable Long usuarioId, @RequestBody Factura facturaRequest) {
        return transaccionService.generarFactura(usuarioId, facturaRequest.getMonto());
    }

    // ✅ Registrar pago desde JSON
    @PostMapping("/pagos/{facturaId}")
    public Pago crearPago(@PathVariable Long facturaId, @RequestBody Pago pagoRequest) {
        return transaccionService.registrarPago(
                facturaId,
                pagoRequest.getMetodoPago(),
                pagoRequest.getMonto()
        );
    }

    // ✅ Listar facturas
    @GetMapping("/facturas")
    public List<Factura> listarFacturas() {
        return transaccionService.listarFacturas();
    }
}
