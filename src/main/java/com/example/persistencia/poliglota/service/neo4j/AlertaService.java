package com.example.persistencia.poliglota.service.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Alerta;
import com.example.persistencia.poliglota.repository.neo4j.AlertaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<Alerta> getAll() {
        return alertaRepository.findAll();
    }

    public Optional<Alerta> getById(Long id) {
        return alertaRepository.findById(id);
    }

    public Alerta save(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    public void delete(Long id) {
        alertaRepository.deleteById(id);
    }
}
