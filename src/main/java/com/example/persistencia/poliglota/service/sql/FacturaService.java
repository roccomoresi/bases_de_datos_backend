package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> getFacturasPorUsuario(Usuario usuario) {
        return facturaRepository.findByUsuario(usuario);
    }

    public Factura crearFactura(Factura factura) {
        return facturaRepository.save(factura);
    }
}

