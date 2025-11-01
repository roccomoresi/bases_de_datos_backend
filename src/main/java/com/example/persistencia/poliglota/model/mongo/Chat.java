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
    private String nombreGrupo; // solo se usa si es grupal
    private String tipo; // "privado" o "grupo"
    private Instant ultimaActualizacion;
    private List<Mensaje> mensajes = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mensaje {
        private String remitente;
        private String contenido;
        private Instant fechaEnvio;
        private boolean leido = false;
    }
}
