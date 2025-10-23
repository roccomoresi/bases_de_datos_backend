package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.service.intergracion.AlertaMongoClient;
import org.springframework.stereotype.Service;

@Service
public class AlertaCassandraService {

    private final AlertaMongoClient alertaMongoClient;

    public AlertaCassandraService(AlertaMongoClient alertaMongoClient) {
        this.alertaMongoClient = alertaMongoClient;
    }

    public void evaluarMedicion(Medicion medicion) {
        double temp = medicion.getTemperatura();
        double hum = medicion.getHumedad();

        // 🔹 Calor extremo
        if (temp > 40) {
            alertaMongoClient.enviarAlerta(
                    medicion.getSensorId(),
                    "climatica",
                    "Temperatura extrema: " + temp + "°C en " + medicion.getCiudad(),
                    medicion.getCiudad(),
                    medicion.getPais(),
                    temp,
                    null,
                    "critica"
            );
        }

        // 🔹 Frío extremo
        if (temp < -5) {
            alertaMongoClient.enviarAlerta(
                    medicion.getSensorId(),
                    "climatica",
                    "Frío extremo: " + temp + "°C en " + medicion.getCiudad(),
                    medicion.getCiudad(),
                    medicion.getPais(),
                    temp,
                    null,
                    "moderada"
            );
        }

        // 🔹 Humedad fuera de rango
        if (hum > 90 || hum < 10) {
            alertaMongoClient.enviarAlerta(
                    medicion.getSensorId(),
                    "climatica",
                    "Humedad fuera de rango: " + hum + "% en " + medicion.getCiudad(),
                    medicion.getCiudad(),
                    medicion.getPais(),
                    null,
                    hum,
                    "moderada"
            );
        }
    }
}
