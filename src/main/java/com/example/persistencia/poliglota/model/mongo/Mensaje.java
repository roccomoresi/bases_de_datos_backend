package com.example.persistencia.poliglota.model.mongo;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Modelo de mensaje usado dentro del documento Chat (embebido).
 * No crea una colección independiente en Mongo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    // 👇 ID propio del mensaje (para ubicarlo dentro del array del Chat)
    private String id = new ObjectId().toHexString();

    private String remitente;      // usuario que envía (email o id)
    private String contenido;      // texto
    private Instant fechaEnvio = Instant.now();

    // 👇 En vez de boolean, guardamos los usuarios que lo leyeron
    @Builder.Default
    private Set<Long> leidoPor = new HashSet<>();
}
