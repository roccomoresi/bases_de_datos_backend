package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.PagoRequest;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.TransaccionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sql")
@CrossOrigin(origins = "*")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // ✅ Crear un nuevo usuario
    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return transaccionService.crearUsuario(usuario.getNombre(), usuario.getEmail());
    }

    // ✅ Crear una nueva factura asociada a un usuario
    @PostMapping("/facturas/{usuarioId}")
    public Factura crearFactura(@PathVariable UUID usuarioId, @RequestBody Factura facturaRequest) {
        return transaccionService.generarFactura(usuarioId, facturaRequest.getMonto());
    }

    // ✅ Registrar un pago asociado a una factura
    @PostMapping("/pagos/{facturaId}")
    public Pago crearPago(@PathVariable UUID facturaId, @RequestBody PagoRequest pagoRequest) {
        return transaccionService.registrarPago(
                facturaId,
                pagoRequest.getMetodoPago(),
                pagoRequest.getMonto()
        );
    }

    // ✅ Listar todas las facturas existentes
    @GetMapping("/facturas")
    public List<Factura> listarFacturas() {
        return transaccionService.listarFacturas();
    }

    // ✅ Listar todos los usuarios (útil para probar)
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return transaccionService.listarUsuarios();
    }
}
