package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double monto;
    private String metodo;
    private LocalDateTime fechaPago;

    @OneToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    public Pago() {}

    public Pago(Double monto, String metodo, LocalDateTime fechaPago, Factura factura) {
        this.monto = monto;
        this.metodo = metodo;
        this.fechaPago = fechaPago;
        this.factura = factura;
    }

    // Getters y setters
    public Long getId() { return id; }
    public Double getMonto() { return monto; }
    public String getMetodo() { return metodo; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public Factura getFactura() { return factura; }

    public void setMonto(Double monto) { this.monto = monto; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public void setFactura(Factura factura) { this.factura = factura; }
}
