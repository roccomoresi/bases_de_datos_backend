package com.example.persistencia.poliglota.dto;

import lombok.Data;

@Data
public class SolicitudProcesoRequest {

    private Integer usuarioId;   // FK → Usuario SQL
    private String procesoId;    // FK → Proceso Mongo o SQL
    private String descripcion;  // (opcional) descripción   amigable
    private String ciudad;
    private String pais;
    private String rangoFechas;

}

