package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<Alerta> getAll() {
        return alertaRepository.findAll();
    }

    public Optional<Alerta> getById(UUID id) {
        return alertaRepository.findById(id);
    }

    public Alerta save(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    public void delete(UUID id) {
        alertaRepository.deleteById(id);
    }
}
