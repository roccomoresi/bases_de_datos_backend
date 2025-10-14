package com.example.persistencia.poliglota.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.UUID;

@Document(collection = "procesos")
public class Proceso {

    @Id
    private UUID id = UUID.randomUUID();

    private String nombre;
    private String tipo;
    private String estado;
    private List<UUID> dependencias; // IDs de otros procesos

    public Proceso() {}

    public Proceso(String nombre, String tipo, String estado, List<UUID> dependencias) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.dependencias = dependencias;
    }

    // Getters y Setters
}
