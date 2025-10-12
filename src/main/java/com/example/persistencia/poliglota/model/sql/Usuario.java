package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String email;
    private String password;

    public Usuario() {} // necesario para JPA

    public Usuario(String nombreCompleto, String email) {
    this.nombreCompleto = nombreCompleto;
    this.email = email;
    }


    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol; // 👈 ESTE campo es el que necesita Rol para "mappedBy = rol"

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
