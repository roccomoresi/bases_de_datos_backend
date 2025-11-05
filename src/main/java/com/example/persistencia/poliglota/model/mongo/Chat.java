package com.example.persistencia.poliglota.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un chat entre usuarios (privado o grupal).
 * Contiene mensajes embebidos directamente en el documento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    private List<String> participantes = new ArrayList<>();  // IDs o nombres de usuario
    private String nombreGrupo;                              // Solo si es grupal
    private String tipo;                                     // "privado" o "grupo"
    private Instant ultimaActualizacion = Instant.now();     // Fecha de última actividad

    private List<Mensaje> mensajes = new ArrayList<>();      // Mensajes embebidos

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mensaje {
        private String remitente;
        private String contenido;
        private Instant fechaEnvio = Instant.now();
        private boolean leido = false;
    }
}
