package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.service.sql.TransaccionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return transaccionService.crearUsuario(usuario.getNombreCompleto(), usuario.getEmail());
    }

    @PostMapping("/facturas/{usuarioId}")
    public Factura crearFactura(@PathVariable Long usuarioId, @RequestParam Double monto) {
        return transaccionService.generarFactura(usuarioId, monto);
    }

    @PostMapping("/pagos/{facturaId}")
    public Pago crearPago(@PathVariable Long facturaId, @RequestParam String metodo, @RequestParam Double monto) {
        return transaccionService.registrarPago(facturaId, metodo, monto);
    }

    @GetMapping("/facturas")
    public List<Factura> listarFacturas() {
        return transaccionService.listarFacturas();
    }
}
