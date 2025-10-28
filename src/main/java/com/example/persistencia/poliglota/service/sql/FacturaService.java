package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FacturaService {
    private final FacturaRepository repository;

    public FacturaService(FacturaRepository repository) {
        this.repository = repository;
    }

    public List<Factura> getAll() {
        return repository.findAll();
    }

    public List<Factura> getByUsuario(Integer usuarioId) {
        return repository.findByUsuario_IdUsuario(usuarioId);
    }

    public Optional<Factura> getById(Integer id) {
        return repository.findById(id);
    }

    public Factura save(Factura factura) {
        return repository.save(factura);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
