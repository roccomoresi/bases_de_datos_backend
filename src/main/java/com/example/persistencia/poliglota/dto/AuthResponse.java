package com.example.persistencia.poliglota.dto;

import com.example.persistencia.poliglota.model.sql.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Usuario usuario;
}
