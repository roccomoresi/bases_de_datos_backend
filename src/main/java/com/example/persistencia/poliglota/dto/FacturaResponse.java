package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FacturaResponse {
    private Integer idFactura;
    private Integer idUsuario;
    private String nombreUsuario;
    private String emailUsuario;
    private String descripcionProceso;
    private String estado;
    private Double total;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaEmision;
}
