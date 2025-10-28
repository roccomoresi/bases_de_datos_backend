package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public FacturaService(FacturaRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Factura> getAll() {
        return repository.findAll();
    }

    public List<Factura> getByUsuario(Integer usuarioId) {
        return repository.findByUsuario_IdUsuario(usuarioId);
    }

    public Factura getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Factura save(Factura factura) {
        return repository.save(factura);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }


public List<Factura> getByEstado(Factura.EstadoFactura estado) {
    return repository.findByEstado(estado);
}


    /* ───────────────────────────────────────────────
       💡 Generar factura automáticamente desde Mongo
    ─────────────────────────────────────────────── */
    public Factura generarFactura(Integer usuarioId, String procesosFacturados, Double monto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setMontoTotal(monto);
        factura.setProcesosFacturados(procesosFacturados);
        factura.setEstado(Factura.EstadoFactura.pendiente);
        factura.setFechaEmision(LocalDateTime.now());

        return repository.save(factura);
    }

    /* 🔄 Actualizar estado de factura */
    public void marcarComoPagada(Factura factura) {
        factura.setEstado(Factura.EstadoFactura.pagada);
        repository.save(factura);
    }
}
