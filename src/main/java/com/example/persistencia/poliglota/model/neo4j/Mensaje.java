package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Node("Mensaje")
public class Mensaje {

    @Id
    private UUID id = UUID.randomUUID();



    private String contenido;
    private LocalDate fecha;

    private String tipo; // privado / grupal

    @Relationship(type = "ENVIADO_POR", direction = Relationship.Direction.OUTGOING)
    private UsuarioNeo remitente;

    @Relationship(type = "DESTINADO_A")
    private UsuarioNeo destinatario;

    public Mensaje(UUID id, String contenido, LocalDate fecha, String tipo, UsuarioNeo remitente,
            UsuarioNeo destinatario) {
        this.id = id;
        this.contenido = contenido;
        this.fecha = fecha;
        this.tipo = tipo;
        this.remitente = remitente;
        this.destinatario = destinatario;
    }

    public Mensaje() {}

    public Mensaje(String contenido, LocalDate fecha) {
        this.contenido = contenido;
        this.fecha = fecha;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getContenido() { return contenido; }
    public LocalDate getFecha() { return fecha; }
    public void setDestinatario(UsuarioNeo destinatario) { this.destinatario = destinatario; }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public UsuarioNeo getRemitente() {
        return remitente;
    }

    public void setRemitente(UsuarioNeo remitente) {
        this.remitente = remitente;
    }

    public UsuarioNeo getDestinatario() {
        return destinatario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
