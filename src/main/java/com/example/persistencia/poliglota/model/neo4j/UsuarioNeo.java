package com.example.persistencia.poliglota.model.neo4j;

import org.springframework.data.neo4j.core.schema.*;

import jnr.ffi.Struct.u_int16_t;

import java.util.List;
import java.util.UUID;

@Node("Usuario")
public class UsuarioNeo {

    @Id
    private UUID id = UUID.randomUUID();



    private String nombre;
    private String email;

    @Relationship(type = "EJECUTA")
    private List<Proceso> procesos;

    @Relationship(type = "COLABORA_CON", direction = Relationship.Direction.OUTGOING)
    private List<UsuarioNeo> colaboradores;

    @Relationship(type = "ENVIA")
    private List<Mensaje> mensajesEnviados;

    public UsuarioNeo() {}

    public UsuarioNeo(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
}
