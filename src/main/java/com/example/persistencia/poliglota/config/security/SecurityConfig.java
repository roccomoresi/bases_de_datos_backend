package com.example.persistencia_poliglota.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Rutas públicas (whitelist)
    private static final String[] WHITELIST = new String[] {
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs/",
            "/error"              // por si Spring devuelve /error en respuestas
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS abierto para pruebas (Swagger, Postman, etc.)
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            // Sin CSRF porque exponemos APIs
            .csrf(csrf -> csrf.disable())
            // APIs sin estado (mejor para backends REST)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Autorizaciones
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITELIST).permitAll()
                // Permitir TODAS las APIs del backend
                .requestMatchers("/api/").permitAll()
                .anyRequest().authenticated()
            )
            // Deshabilitamos formLogin y dejamos httpBasic por si algún día lo usan
            .formLogin(form -> form.disable())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // CORS: todos los orígenes, métodos y headers (ideal para QA)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(false);
        cfg.setExposedHeaders(List.of("Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/", cfg);
        return source;
    }
} 