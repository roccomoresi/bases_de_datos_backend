package com.example.persistencia.poliglota.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Modelo de mensaje usado dentro del documento Chat.
 * No crea una colección independiente en Mongo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    private String remitente;       // Usuario que envía el mensaje
    private String contenido;       // Texto del mensaje
    private Instant fechaEnvio;     // Fecha y hora en formato UTC
    private boolean leido = false;  // Estado de lectura

}
