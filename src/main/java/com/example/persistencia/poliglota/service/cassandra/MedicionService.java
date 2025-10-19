package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorCiudadRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MedicionService {

    private final MedicionRepository medicionRepository;
    private final MedicionPorCiudadRepository medicionPorCiudadRepository;

    public MedicionService(MedicionRepository medicionRepository, MedicionPorCiudadRepository medicionPorCiudadRepository) {
        this.medicionRepository = medicionRepository;
        this.medicionPorCiudadRepository = medicionPorCiudadRepository;
    }

    public List<Medicion> obtenerPorSensor(UUID sensorId) {
        return medicionRepository.findBySensorId(sensorId);
    }

    public List<Medicion> obtenerPorRango(UUID sensorId, Instant desde, Instant hasta) {
        return medicionRepository.findBySensorIdAndFechaBetween(sensorId, desde, hasta);
    }

    public List<MedicionPorCiudad> obtenerPorCiudad(String ciudad, String pais) {
        return medicionPorCiudadRepository.findByCiudadAndPais(ciudad, pais);
    }

    public Medicion guardar(Medicion medicion) {
        // Guardar en ambas tablas (desnormalización)
        MedicionPorCiudad medicionCiudad = new MedicionPorCiudad(
                medicion.getCiudad(),
                medicion.getPais(),
                medicion.getFechaMedicion(),
                medicion.getSensorId(),
                medicion.getTemperatura(),
                medicion.getHumedad()
        );

        medicionPorCiudadRepository.save(medicionCiudad);
        return medicionRepository.save(medicion);
    }
}
