package com.example.persistencia.poliglota.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "5F7C6A745D2B4A614E645267556B58703273357638792F423F4528482B4D6251"; // 64 chars

    // 🔹 Genera un token nuevo
    public String generarToken(Map<String, Object> claims, String username, String rol) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .claim("role", rol)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Genera un token sin claims extra
    public String generarToken(String username, String rol) {
        return generarToken(new HashMap<>(), username, rol);
    }

    // 🔹 Extrae el username (subject)
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // 🔹 Valida si el token pertenece a ese user y no expiró
    public boolean esTokenValido(String token, String username) {
        final String usernameExtraido = extraerUsername(token);
        return (usernameExtraido.equals(username)) && !estaExpirado(token);
    }

    // 🔹 Extrae cualquier claim
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean estaExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
