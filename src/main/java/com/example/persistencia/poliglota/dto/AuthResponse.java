package com.example.persistencia.poliglota.dto;

import com.example.persistencia.poliglota.model.sql.Usuario;

public class AuthResponse {
    private String token;
    private Usuario usuario;

    public AuthResponse(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }
    public String getToken() { return token; }
    public Usuario getUsuario() { return usuario; }
}
