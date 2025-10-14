package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "alertas")
public class Alerta {

    @Id
    private UUID id = UUID.randomUUID();

    private String tipo;        // "sensor" o "climatica"
    private String descripcion; // motivo o detalle de la alerta
    private String estado;      // "activa", "resuelta"
    private LocalDateTime fechaHora;

    private UUID sensorId;      // referencia al sensor (de Cassandra)

    public Alerta() {}

    public Alerta(String tipo, String descripcion, String estado, LocalDateTime fechaHora, UUID sensorId) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.sensorId = sensorId;
    }

    // Getters y Setters
}
