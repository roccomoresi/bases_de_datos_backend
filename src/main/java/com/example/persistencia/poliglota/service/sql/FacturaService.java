package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Crear factura asociada a un usuario
    public Factura crearFactura(UUID usuarioId, Double monto, String fecha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura();
        factura.setMonto(monto);
        factura.setFechaEmision(LocalDate.parse(fecha));
        factura.setUsuario(usuario);
        factura.setEstado("PENDIENTE");

        return facturaRepository.save(factura);
    }

    // ✅ Obtener todas las facturas
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    // ✅ Obtener facturas por usuario (usado en ReporteController)
    public List<Factura> getFacturasPorUsuario(Usuario usuario) {
        return facturaRepository.findByUsuario(usuario);
    }
}
