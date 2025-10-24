package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.ControlFuncionamiento;
import com.example.persistencia.poliglota.repository.cassandra.ControlFuncionamientoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ControlFuncionamientoService {

    private final ControlFuncionamientoRepository repository;

    public ControlFuncionamientoService(ControlFuncionamientoRepository repository) {
        this.repository = repository;
    }

    // ✅ Último control (más reciente)
    public Optional<ControlFuncionamiento> getUltimoControlPorSensor(UUID sensorId) {
        List<ControlFuncionamiento> controles = repository.findBySensorId(sensorId);
        return controles.stream().max(Comparator.comparing(ControlFuncionamiento::getFechaControl));
    }

    // ✅ Todos los controles de un sensor
    public List<ControlFuncionamiento> getControlesPorSensor(UUID sensorId) {
        return repository.findBySensorId(sensorId);
    }

    // ✅ Crear o guardar un control
    public ControlFuncionamiento guardarControl(ControlFuncionamiento control) {
        if (control.getFechaControl() == null) {
            control.setFechaControl(Instant.now());
        }
        return repository.save(control);
    }
}
