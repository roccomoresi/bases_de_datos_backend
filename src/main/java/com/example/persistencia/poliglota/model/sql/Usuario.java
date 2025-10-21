package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombreCompleto;
    private String email;
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado = EstadoUsuario.activo;

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    public enum EstadoUsuario {
        activo, inactivo
    }
}
