package com.example.persistencia.poliglota.service.intergracion;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlertaMongoClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ALERTAS_MONGO_URL = "http://localhost:8080/api/mongo/alertas";

    public void enviarAlerta(UUID sensorId, String tipo, String descripcion, String ciudad, String pais,
                             Double temperatura, Double humedad, String severidad) {
        try {
            Map<String, Object> detalles = new HashMap<>();
            if (temperatura != null) detalles.put("temperatura", temperatura);
            if (humedad != null) detalles.put("humedad", humedad);
            detalles.put("fuente", "cassandra");

            Map<String, Object> alerta = Map.of(
                    "sensorId", sensorId != null ? sensorId.toString() : null,
                    "tipo", tipo,
                    "descripcion", descripcion,
                    "ciudad", ciudad,
                    "pais", pais,
                    "severidad", severidad,
                    "detalles", detalles
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(alerta, headers);

            restTemplate.postForEntity(ALERTAS_MONGO_URL, request, String.class);
            System.out.println("✅ Alerta enviada a Mongo: " + descripcion);
        } catch (Exception e) {
            System.err.println("❌ Error enviando alerta a Mongo: " + e.getMessage());
        }
    }
}
