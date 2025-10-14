package com.example.persistencia.poliglota.dto;

public class PagoRequest {
    private String metodoPago;
    private Double monto;

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}
