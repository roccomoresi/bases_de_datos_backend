package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.UUID;

@Document(collection = "usuarios_doc")
public class UsuarioMongo {

    @Id
    private UUID id = UUID.randomUUID();

    private String nombre;
    private String email;
    private List<UUID> procesos;        // procesos ejecutados
    private List<UUID> colaboradores;   // otros usuarios
    private List<UUID> mensajesEnviados;

    public UsuarioMongo() {}
    public UsuarioMongo(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y Setters
}
