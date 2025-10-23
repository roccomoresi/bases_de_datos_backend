package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorCiudadRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@Service
public class MedicionService {

    private final MedicionRepository medicionRepository;
    private final MedicionPorCiudadRepository medicionPorCiudadRepository;

    public MedicionService(MedicionRepository medicionRepository,
                           MedicionPorCiudadRepository medicionPorCiudadRepository) {
        this.medicionRepository = medicionRepository;
        this.medicionPorCiudadRepository = medicionPorCiudadRepository;
    }

    // 🔹 Obtener todas las mediciones por sensor
    public List<Medicion> obtenerPorSensor(UUID sensorId) {
        return medicionRepository.findBySensorId(sensorId);
    }

    // 🔹 Obtener mediciones de un sensor dentro de un rango de fechas
    public List<Medicion> obtenerPorSensorYRangoFechas(UUID sensorId, String desdeStr, String hastaStr) {
    try {
        // Armamos formato con hora completa
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // Si el front solo manda la fecha (sin hora), completamos con 00:00:00 y 23:59:59
        String desdeCompleto = desdeStr.contains("T") ? desdeStr : desdeStr + "T00:00:00";
        String hastaCompleto = hastaStr.contains("T") ? hastaStr : hastaStr + "T23:59:59";

        java.util.Date desde = sdf.parse(desdeCompleto);
        java.util.Date hasta = sdf.parse(hastaCompleto);

        return medicionRepository.findBySensorIdAndFechaBetween(sensorId, desde, hasta);
    } catch (Exception e) {
        throw new RuntimeException("Error al parsear fechas: " + e.getMessage());
    }
}


    // 🔹 Obtener mediciones por ciudad y país
    public List<MedicionPorCiudad> obtenerPorCiudad(String ciudad, String pais) {
        return medicionPorCiudadRepository.findByCiudadAndPais(ciudad, pais);
    }

    // 🔹 Guardar medición (en ambas tablas desnormalizadas)
    public Medicion guardar(Medicion medicion) {
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
