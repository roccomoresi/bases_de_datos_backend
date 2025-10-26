package com.example.persistencia.poliglota.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
    Integer idUsuario,
    String nombreCompleto,
    String email,
    String rol,
    String estado,
    LocalDateTime fechaRegistro
) {}

