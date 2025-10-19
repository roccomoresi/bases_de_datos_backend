package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.ControlFuncionamiento;
import com.example.persistencia.poliglota.repository.cassandra.ControlFuncionamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ControlFuncionamientoService {

    private final ControlFuncionamientoRepository repo;

    public ControlFuncionamientoService(ControlFuncionamientoRepository repo) {
        this.repo = repo;
    }

    public ControlFuncionamiento guardar(ControlFuncionamiento control) {
        return repo.save(control);
    }

    public List<ControlFuncionamiento> obtenerPorSensor(UUID sensorId) {
        return repo.findBySensorId(sensorId);
    }
}
