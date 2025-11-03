package com.example.persistencia.poliglota.config.security;

import com.example.persistencia.poliglota.service.sql.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String email = jwtService.extraerEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            var usuarioOpt = usuarioService.buscarPorEmail(email);

            if (usuarioOpt.isPresent() && jwtService.validarToken(token, email)) {

                // 🔹 Extrae el rol (compatibilidad con "rol" o "role")
                String rol = jwtService.extraerRol(token);
                if (rol == null || rol.isBlank()) {
                    // fallback manual si el token tiene otra clave
                    var claims = jwtService.extraerTodosLosClaims(token);
                    rol = claims.get("role", String.class);
                }

                // 🔹 Normaliza formato ROLE_*
                if (rol != null && !rol.startsWith("ROLE_")) {
                    rol = "ROLE_" + rol.toUpperCase();
                }

                var authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuarioOpt.get(), null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("✅ Token válido: {} ({})", email, rol);
            } else {
                log.warn("⚠️ Token inválido o usuario no encontrado: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
