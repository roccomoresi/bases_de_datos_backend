package com.example.persistencia.poliglota.dto;

import java.time.LocalDateTime;

public record SesionResponse(
        Long idSesion,
        Integer usuarioId,
        String nombre,
        String email,
        String rol,
        LocalDateTime fechaInicio,
        String estado
) {}
