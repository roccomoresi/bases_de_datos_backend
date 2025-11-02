package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private Integer idFactura;
    private Double monto;
    private String metodoPago;
}
