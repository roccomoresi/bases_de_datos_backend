package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sesion")
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSesion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String rol;
    private LocalDateTime fechaInicio = LocalDateTime.now();
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    private EstadoSesion estadoActual = EstadoSesion.activa;

    public enum EstadoSesion {
        activa, inactiva
    }
}
