package com.example.persistencia.poliglota.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ Permitir preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ Endpoints públicos (login, register, docs, etc.)
                .requestMatchers(
                    "/auth/**",
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/usuarios/register",
                    "/api/usuarios/login"
                ).permitAll()

                // 🔐 Endpoints protegidos
                .requestMatchers("/api/sql/usuarios/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/sql/**", "/api/finanzas/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")
                .requestMatchers("/api/monitoreo/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")
                .requestMatchers("/api/informes/**", "/api/mongo/**")
                    .authenticated()

                // 🔒 Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            // ✅ Muy importante: agregar el filtro JWT DESPUÉS de permitir /auth/**
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
