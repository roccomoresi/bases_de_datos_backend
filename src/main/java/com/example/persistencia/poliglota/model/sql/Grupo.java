package com.example.persistencia.poliglota.model.mongo;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "grupos")
public class Grupo {

    @Id
    private String id;
    private String nombre;
    private List<String> miembros; // IDs de usuarios

    // Getters y Setters
}

