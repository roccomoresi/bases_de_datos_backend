package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    private final UsuarioRepository usuarioRepo;
    private final FacturaRepository facturaRepo;
    private final PagoRepository pagoRepo;

    public TransaccionService(UsuarioRepository usuarioRepo, FacturaRepository facturaRepo, PagoRepository pagoRepo) {
        this.usuarioRepo = usuarioRepo;
        this.facturaRepo = facturaRepo;
        this.pagoRepo = pagoRepo;
    }

    public Usuario crearUsuario(String nombre, String email) {
    boolean existe = usuarioRepo.findAll().stream()
            .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    if (existe) {
        throw new IllegalArgumentException("El email '" + email + "' ya está registrado.");
    }
    return usuarioRepo.save(new Usuario(nombre, email));
}


    public Factura generarFactura(Long usuarioId, Double monto) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        Factura factura = new Factura(LocalDate.now(), monto, usuario);
        return facturaRepo.save(factura);
    }

    public Pago registrarPago(Long facturaId, String metodo, Double monto) {
    Factura factura = facturaRepo.findById(facturaId).orElseThrow();

    // Evitar pagar una factura ya pagada
    if ("PAGADA".equalsIgnoreCase(factura.getEstado())) {
        throw new IllegalStateException("La factura ya fue pagada anteriormente.");
    }

    // Crear el pago
    Pago pago = new Pago(monto, metodo, LocalDateTime.now(), factura);
    pagoRepo.save(pago);

    // 🔁 Actualizar el estado de la factura
    factura.setEstado("PAGADA");
    facturaRepo.save(factura);

    // 🔄 Recargar la factura actualizada y asignarla al pago
    Pago pagoActualizado = pagoRepo.findById(pago.getId()).orElseThrow();
    Factura facturaActualizada = facturaRepo.findById(facturaId).orElseThrow();
    pagoActualizado.setFactura(facturaActualizada);

    return pagoActualizado;
}




    public List<Factura> listarFacturas() {
        return facturaRepo.findAll();
    }
}
