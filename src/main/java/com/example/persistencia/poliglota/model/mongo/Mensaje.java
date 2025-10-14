package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "mensajes")
public class Mensaje {

    @Id
    private UUID id = UUID.randomUUID();

    private String contenido;       // texto del mensaje
    private String tipo;            // "privado" o "grupal"
    private LocalDate fecha;        // fecha de envío
    private UUID remitenteId;       // referencia al usuario que envía (de MySQL o Mongo)
    private UUID destinatarioId;    // referencia al usuario o grupo receptor

    // 🔹 Constructor vacío requerido por Spring
    public Mensaje() {}

    // 🔹 Constructor con parámetros (para crear rápido desde código)
    public Mensaje(String contenido, String tipo, LocalDate fecha, UUID remitenteId, UUID destinatarioId) {
        this.contenido = contenido;
        this.tipo = tipo;
        this.fecha = fecha;
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
    }

    // 🔹 Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public UUID getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(UUID remitenteId) {
        this.remitenteId = remitenteId;
    }

    public UUID getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(UUID destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    // 🔹 toString (útil para logs o debugging)
    @Override
    public String toString() {
        return "Mensaje{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fecha=" + fecha +
                ", remitenteId=" + remitenteId +
                ", destinatarioId=" + destinatarioId +
                '}';
    }
}
