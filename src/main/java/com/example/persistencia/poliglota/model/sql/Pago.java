package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    private LocalDateTime fechaPago = LocalDateTime.now();
    private Double monto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago = MetodoPago.otros;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado = EstadoPago.confirmado;

    public enum MetodoPago { tarjeta, transferencia, efectivo, otros }
    public enum EstadoPago { confirmado, pendiente, rechazado }
    public Integer getIdPago() {
        return idPago;
    }
    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }
    public Factura getFactura() {
        return factura;
    }
    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    public Double getMonto() {
        return monto;
    }
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    public EstadoPago getEstado() {
        return estado;
    }
    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
}


