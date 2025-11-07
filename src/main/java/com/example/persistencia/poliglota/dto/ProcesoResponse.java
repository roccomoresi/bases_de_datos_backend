package com.example.persistencia.poliglota.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoResponse {
    private String id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private Double costo;
    private boolean activo;
}
