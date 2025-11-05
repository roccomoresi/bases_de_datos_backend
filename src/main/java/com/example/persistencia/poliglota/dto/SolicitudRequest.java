package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class SolicitudRequest {
    private Integer usuarioId;
    private String procesoId;
}
