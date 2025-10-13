package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.PagoRequest;
import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.service.sql.TransaccionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sql")
@CrossOrigin
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return transaccionService.crearUsuario(usuario.getNombre(), usuario.getEmail());
    }

    // ✅ NUEVO: ruta corregida
    @PostMapping("/transacciones/facturas/{usuarioId}")
    public Factura crearFactura(@PathVariable UUID usuarioId, @RequestBody Factura facturaRequest) {
        return transaccionService.generarFactura(usuarioId, facturaRequest.getMonto());
    }

    @PostMapping("/pagos/{facturaId}")
    public Pago crearPago(@PathVariable Long facturaId, @RequestBody PagoRequest pagoRequest) {
        return transaccionService.registrarPago(
            facturaId,
            pagoRequest.getMetodoPago(),
            pagoRequest.getMonto()
    );
}


    @GetMapping("/facturas")
    public List<Factura> listarFacturas() {
        return transaccionService.listarFacturas();
    }
}
