package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private Integer idPago;
    private Integer idFactura;
    private Integer idUsuario;
    private String metodoPago;
    private Double montoPagado;
    private String fechaPago;
}
