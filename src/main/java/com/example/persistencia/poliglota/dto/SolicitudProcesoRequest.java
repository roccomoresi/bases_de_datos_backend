package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class SolicitudProcesoRequest {
    private Integer usuarioId;
    private String procesoId;
    private String tipo;
    private String ciudad;
    private String pais;
    private String desde;
    private String hasta;
}

