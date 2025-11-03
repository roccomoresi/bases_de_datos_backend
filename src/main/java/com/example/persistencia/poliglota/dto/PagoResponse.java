package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResponse {
    private Integer idPago;
    private String metodoPago;
    private Double montoPagado;
    private Integer idFactura;
    private String estadoFactura;
    private String descripcionProceso;
}
