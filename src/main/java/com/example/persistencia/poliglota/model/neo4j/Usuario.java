package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("Usuario")
public class Usuario {

    @Id
    private String id = java.util.UUID.randomUUID().toString();


    private String nombre;
    private String email;

    @Relationship(type = "EJECUTA")
    private List<Proceso> procesos;

    @Relationship(type = "COLABORA_CON", direction = Relationship.Direction.OUTGOING)
    private List<Usuario> colaboradores;

    @Relationship(type = "ENVIA")
    private List<Mensaje> mensajesEnviados;

    public Usuario() {}

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
}
