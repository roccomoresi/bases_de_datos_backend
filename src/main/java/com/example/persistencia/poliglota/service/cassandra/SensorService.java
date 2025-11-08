package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<Sensor> getAll() {
        return sensorRepository.findAll();
    }

    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public List<Sensor> buscarPorCiudad(String ciudad) {
        return sensorRepository.findByCiudad(ciudad);
    }

    public List<Sensor> buscarPorEstado(String estado) {
        return sensorRepository.findByEstado(estado);
    }

 public Optional<Sensor> getById(UUID id) {
    return sensorRepository.findById(id);
}


    public List<Sensor> buscarPorTipo(String tipo) {
    return sensorRepository.findByTipo(tipo);
}

public List<Sensor> buscarPorNombre(String nombre) {
    return sensorRepository.findByNombre(nombre);
}

public void updateEstado(UUID id, String nuevoEstado) {
    sensorRepository.updateEstado(id, nuevoEstado);
}




}
