package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String contenido;
    private String tipo; // privado o grupal
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "remitente_id")
    private Usuario remitente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = true)
    private Usuario destinatario; // null si es grupal

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = true)
    private Grupo grupo; // null si es privado

    // Getters y Setters
}
