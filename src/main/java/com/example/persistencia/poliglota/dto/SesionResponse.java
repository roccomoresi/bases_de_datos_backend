package com.example.persistencia.poliglota.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponse {
    private Long idSesion;
    private Integer usuarioId;
    private String nombre;
    private String email;
    private String rol;
    private LocalDateTime fechaInicio;
    private String estado; // "ACTIVA" o "INACTIVA"
}
