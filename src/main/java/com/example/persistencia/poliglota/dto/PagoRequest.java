package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private Double monto;
    private String metodoPago;
}
