package com.example.persistencia.poliglota.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.persistencia.poliglota.model.sql.Usuario;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    /* ───────────────────────────────
       🔐 GENERAR TOKEN
    ─────────────────────────────── */
    public String generarToken(Usuario usuario) {
        String descripcion = usuario.getRol() != null
                ? usuario.getRol().getDescripcion().toUpperCase()
                : "USUARIO";

        // 👇 Normaliza los roles al formato estándar de Spring
        String formattedRole = switch (descripcion) {
            case "ADMIN", "ADMINISTRADOR" -> "ROLE_ADMIN";
            case "TECNICO", "TÉCNICO" -> "ROLE_TECNICO";
            default -> "ROLE_USUARIO";
        };

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("role", formattedRole)
                .claim("id_usuario", usuario.getIdUsuario())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ───────────────────────────────
       🔎 VALIDAR TOKEN
    ─────────────────────────────── */
    public boolean validarToken(String token, String email) {
        try {
            final String username = extraerEmail(token);
            return (username.equals(email) && !estaExpirado(token));
        } catch (Exception e) {
            log.warn("⚠️ Token inválido o corrupto: {}", e.getMessage());
            return false;
        }
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
        String role = (String) claims.get("role");

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
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
