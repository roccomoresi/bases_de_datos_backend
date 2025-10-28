package com.example.persistencia.poliglota.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    private List<String> participantes = new ArrayList<>();

    private List<Mensaje> mensajes = new ArrayList<>();

    private Instant ultimaActualizacion = Instant.now();

    // Clase interna para representar los mensajes del chat
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mensaje {
        private String remitente;
        private String contenido;
        private Instant fechaEnvio;
    }
}
