package com.example.persistencia.poliglota.config.security;

import com.example.persistencia.poliglota.service.sql.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

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
        final String token;
        final String email;

        // Si no hay token, seguimos con el flujo
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        email = jwtService.extraerEmail(token);

        // Si hay un token pero el usuario aún no está autenticado
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            var usuarioOpt = usuarioService.buscarPorEmail(email);
            if (usuarioOpt.isPresent() && jwtService.validarToken(token, email)) {
                String rol = jwtService.extraerRol(token); // ej: ROLE_ADMIN
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
