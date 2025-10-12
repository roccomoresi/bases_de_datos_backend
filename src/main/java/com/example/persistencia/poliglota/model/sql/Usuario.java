package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(unique = true)
    private String email;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Factura> facturas;

    public Usuario() {}

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }
}
