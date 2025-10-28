package com.example.persistencia.poliglota.service.intergracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlertaMongoClient {

    private static final Logger log = LoggerFactory.getLogger(AlertaMongoClient.class);

    private final RestTemplate restTemplate;
    private static final String ALERTAS_MONGO_URL = "http://localhost:8080/api/mongo/alertas";

    public AlertaMongoClient() {
        this.restTemplate = new RestTemplate();
    }

    public void enviarAlerta(
            UUID sensorId,
            String tipo,
            String descripcion,
            String ciudad,
            String pais,
            Double temperatura,
            Double humedad,
            String severidad
    ) {
        try {
            // --- cuerpo.detalles ---
            Map<String, Object> detalles = new HashMap<>();
            if (temperatura != null) detalles.put("temperatura", temperatura);
            if (humedad != null) detalles.put("humedad", humedad);
            detalles.put("fuente", "cassandra");
            detalles.put("demo", false);

            // --- cuerpo principal ---
            Map<String, Object> alerta = new HashMap<>();
            alerta.put("sensorId", sensorId != null ? sensorId.toString() : null);
            alerta.put("tipo", tipo);                       // ej: "climatica"
            alerta.put("descripcion", descripcion);         // mensaje
            alerta.put("ciudad", ciudad);
            alerta.put("pais", pais);
            alerta.put("severidad", severidad);             // ej: "alta", "media"
            alerta.put("estado", "activa");
            alerta.put("fecha", Instant.now().toString());  // 👈 ahora se llama "fecha"
            alerta.put("fuente", "cassandra");
            alerta.put("detalles", detalles);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(alerta, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ALERTAS_MONGO_URL,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Alerta enviada correctamente a Mongo: [{}] {}", tipo, descripcion);
            } else {
                log.warn("⚠️ Alerta enviada pero con código HTTP inesperado: {}", response.getStatusCode());
            }

        } catch (HttpStatusCodeException ex) {
            log.error("❌ Error HTTP al enviar alerta a Mongo ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("❌ Error general enviando alerta a Mongo: {}", e.getMessage());
        }
    }
}
