package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "mensajes")
public class Mensaje {

    @Id
    private String id;

    private String remitente;
    private String destinatario; // usuario o grupo
    private String contenido;
    private String tipo; // privado o grupal
    private LocalDateTime fechaHora;

    // Getters y Setters
}
