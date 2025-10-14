package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "mensajes")
public class Mensaje {

    @Id
    private UUID id = UUID.randomUUID();

    private String contenido;
    private String tipo;        // "privado" o "grupal"
    private LocalDate fecha;
    private UUID remitenteId;   // usuario SQL
    private UUID destinatarioId; // usuario SQL o grupo SQL

    public Mensaje() {}

    public Mensaje(String contenido, String tipo, LocalDate fecha, UUID remitenteId, UUID destinatarioId) {
        this.contenido = contenido;
        this.tipo = tipo;
        this.fecha = fecha;
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
    }

    // Getters y Setters
}
