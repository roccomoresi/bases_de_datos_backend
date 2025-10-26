package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    @Column(nullable = false, unique = true, length = 50)
    private String descripcion; // Ej: "ADMIN", "TECNICO", "USUARIO"
}
