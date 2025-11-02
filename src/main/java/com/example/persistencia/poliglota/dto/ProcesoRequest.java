package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class ProcesoRequest {
    private String tipo;
    private String ciudad;
    private String desde;
    private String hasta;
}

