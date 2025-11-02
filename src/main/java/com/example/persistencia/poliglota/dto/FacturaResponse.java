package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FacturaResponse {
    private Integer idFactura;
    private Integer usuarioId;
    private LocalDateTime fechaEmision;
    private String estado;
    private Double total;
    private String descripcionProceso;
}
