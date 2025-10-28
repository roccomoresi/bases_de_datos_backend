package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cuenta_corriente")
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCuenta;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    private Double saldoActual = 0.0;

    private String historialMovimientos; 
    // Ej: "Factura #3 -$200 | Pago #5 +$200"

    public void agregarMovimiento(String movimiento) {
        if (historialMovimientos == null) historialMovimientos = "";
        historialMovimientos += movimiento + " | ";
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(Double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public String getHistorialMovimientos() {
        return historialMovimientos;
    }

    public void setHistorialMovimientos(String historialMovimientos) {
        this.historialMovimientos = historialMovimientos;
    }
}


