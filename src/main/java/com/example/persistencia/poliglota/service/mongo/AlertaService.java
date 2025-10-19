package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public void crear(UUID sensorId, String tipo, String descripcion, String ciudad, String pais) {
        Alerta alerta = new Alerta(tipo, sensorId, descripcion, ciudad, pais);
        alertaRepository.save(alerta);
        System.out.println("🚨 Alerta creada: " + alerta.getDescripcion());
    }

    public void resolver(UUID id) {
        alertaRepository.findById(id).ifPresent(alerta -> {
            alerta.setEstado("resuelta");
            alertaRepository.save(alerta);
            System.out.println("✅ Alerta resuelta: " + id);
        });
    }
}
