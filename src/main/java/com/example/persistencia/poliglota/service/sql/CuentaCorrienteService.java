package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.repository.sql.CuentaCorrienteRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaCorrienteService {
    private final CuentaCorrienteRepository repository;

    public CuentaCorrienteService(CuentaCorrienteRepository repository) {
        this.repository = repository;
    }

    public List<CuentaCorriente> getAll() {
        return repository.findAll();
    }

    public CuentaCorriente getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioIdUsuario(usuarioId);
    }

    public CuentaCorriente save(CuentaCorriente cuenta) {
        return repository.save(cuenta);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByUsuarioId(Integer usuarioId) {
    repository.deleteByUsuario_IdUsuario(usuarioId);
}
}
