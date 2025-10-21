package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {
    private final PagoRepository repository;

    public PagoService(PagoRepository repository) {
        this.repository = repository;
    }

    public List<Pago> getAll() {
        return repository.findAll();
    }

    public List<Pago> getByFactura(Integer facturaId) {
        return repository.findByFacturaIdFactura(facturaId);
    }

    public Pago save(Pago pago) {
        return repository.save(pago);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
