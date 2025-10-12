package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.util.List;

import com.example.persistencia.poliglota.model.neo4j.Usuario;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion; // usuario, técnico, administrador

    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

    // Getters y Setters
}

