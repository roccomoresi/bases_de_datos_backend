package com.example.persistencia.poliglota.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequest {
    private Double monto;
    private String fecha;
}
