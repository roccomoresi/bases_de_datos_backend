package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaCreateRequest {
    private Integer idUsuario;
    private Double total;
    private String descripcionProceso; // opcional, referencia al proceso en Mongo
}
