package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private Double montoTotal;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioSQL usuario;

    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL)
    private Pago pago;

    public Factura() {}

    public Factura(LocalDate fecha, Double montoTotal, UsuarioSQL usuario) {
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.usuario = usuario;
    }

    // Getters y setters
    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public Double getMontoTotal() { return montoTotal; }
    public UsuarioSQL getUsuario() { return usuario; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
    public void setUsuario(UsuarioSQL usuario) { this.usuario = usuario; }
}
