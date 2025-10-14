package com.example.persistencia.poliglota.service;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.*;
import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransaccionService {

    private final UsuarioRepository usuarioRepository;
    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final AlertaRepository alertaRepository; // ✅ MongoDB

    public TransaccionService(UsuarioRepository usuarioRepository,
                              FacturaRepository facturaRepository,
                              PagoRepository pagoRepository,
                              AlertaRepository alertaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.alertaRepository = alertaRepository;
    }

    // ✅ Crear usuario (MySQL)
    public Usuario crearUsuario(String nombre, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        return usuarioRepository.save(usuario);
    }

    // ✅ Generar factura (MySQL)
    public Factura generarFactura(UUID usuarioId, Double monto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura(LocalDate.now(), monto, usuario);
        factura.setEstado("PENDIENTE");
        facturaRepository.save(factura);

        // Registrar alerta en Mongo
        Alerta alerta = new Alerta(
                "facturacion",
                "Factura generada para el usuario: " + usuario.getNombre(),
                "activa",
                LocalDateTime.now(),
                null,
                "baja"
        );
        alertaRepository.save(alerta);

        return factura;
    }

    public Pago registrarPago(UUID facturaId, String metodoPago, Double monto) {
    Factura factura = facturaRepository.findById(facturaId)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

    Pago pago = new Pago(monto, metodoPago, LocalDateTime.now(), factura);
    pagoRepository.save(pago);

    factura.setEstado("PAGADA");
    facturaRepository.save(factura);

    // Crear alerta informativa en Mongo
    Alerta alerta = new Alerta(
            "pago",
            "Pago registrado correctamente por $" + monto + " en factura ID " + facturaId,
            "activa",
            LocalDateTime.now(),
            null,
            "media"
    );
    alertaRepository.save(alerta);

    return pago;
}


    // ✅ Listar facturas (MySQL)
    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public List<Usuario> listarUsuarios() {
    return usuarioRepository.findAll();
}

}
