package com.example.persistencia.poliglota.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.persistencia.poliglota.model.sql.Usuario;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}") // podés moverla a application.properties
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    /* ───────────────────────────────
       🔐 GENERAR TOKEN
    ─────────────────────────────── */
public String generarToken(Usuario usuario) {
    // Normaliza el rol a formato ROLE_*
    String formattedRole = usuario.getRol() != null && !usuario.getRol().getDescripcion().startsWith("ROLE_")
            ? "ROLE_" + usuario.getRol().getDescripcion().toUpperCase()
            : usuario.getRol().getDescripcion();

    return Jwts.builder()
            .setSubject(usuario.getEmail()) // 👈 email
            .claim("rol", formattedRole)     // 👈 rol
            .claim("id_usuario", usuario.getIdUsuario()) // 👈 NUEVO: ID DEL USUARIO
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
            .compact();
}


    /* ───────────────────────────────
       🔎 VALIDAR TOKEN
    ─────────────────────────────── */
    public boolean validarToken(String token, String email) {
        final String username = extraerEmail(token);
        return (username.equals(email) && !estaExpirado(token));
    }

    /* ───────────────────────────────
       📧 EXTRAER EMAIL
    ─────────────────────────────── */
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /* ───────────────────────────────
       👮‍♂️ EXTRAER ROL
    ─────────────────────────────── */
   public String extraerRol(String token) {
    Claims claims = extraerTodosLosClaims(token);
    String role = (String) (claims.get("rol") != null ? claims.get("rol") : claims.get("role"));

    // Asegura el prefijo ROLE_
    if (role != null && !role.startsWith("ROLE_")) {
        role = "ROLE_" + role.toUpperCase();
    }
    return role;
}


    /* ───────────────────────────────
       🧩 MÉTODOS INTERNOS
    ─────────────────────────────── */
    private boolean estaExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
}

}
