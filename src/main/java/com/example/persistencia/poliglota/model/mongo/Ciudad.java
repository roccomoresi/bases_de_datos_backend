package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document(collection = "ciudades")
public class Ciudad {

    @Id
    private UUID id = UUID.randomUUID();
    private String nombre;
    private String pais;

    public Ciudad() {}
    public Ciudad(String nombre, String pais) {
        this.nombre = nombre;
        this.pais = pais;
    }

    // Getters y Setters
}
