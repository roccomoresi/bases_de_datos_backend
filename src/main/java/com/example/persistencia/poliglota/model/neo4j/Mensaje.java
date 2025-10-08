package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;

@Node("Mensaje")
public class Mensaje {

    @Id
    private String id = java.util.UUID.randomUUID().toString();


    private String contenido;
    private LocalDate fecha;

    @Relationship(type = "DESTINADO_A")
    private Usuario destinatario;

    public Mensaje() {}

    public Mensaje(String contenido, LocalDate fecha) {
        this.contenido = contenido;
        this.fecha = fecha;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getContenido() { return contenido; }
    public LocalDate getFecha() { return fecha; }
    public void setDestinatario(Usuario destinatario) { this.destinatario = destinatario; }
}
