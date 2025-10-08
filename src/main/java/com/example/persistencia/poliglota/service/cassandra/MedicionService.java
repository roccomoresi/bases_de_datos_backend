package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MedicionService {

    private final MedicionRepository medicionRepository;

    public MedicionService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public List<Medicion> obtenerPorSensor(UUID sensorId) {
        return medicionRepository.findBySensor(sensorId);
    }

    public List<Medicion> obtenerPorRango(UUID sensorId, Instant desde, Instant hasta) {
        return medicionRepository.findBySensorAndTimestampBetween(sensorId, desde, hasta);
    }

    public Medicion guardar(Medicion medicion) {
        return medicionRepository.save(medicion);
    }
}
