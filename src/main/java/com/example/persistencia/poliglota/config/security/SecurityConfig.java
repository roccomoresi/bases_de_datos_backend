package com.example.persistencia.poliglota.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ✅ RUTAS LIBRES (SIN TOKEN)
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/error",
                        "/api/auth/**",
                        "/api/mongo/alertas"      // LOGIN / REGISTER LIBRE
                ).permitAll()

                // 👥 Usuarios / Roles / Sesiones → SOLO ADMIN
                .requestMatchers("/api/sql/usuarios/**", "/api/sql/roles/**", "/api/sql/sesiones/**").permitAll()
                    //.hasAuthority("ROLE_ADMIN")

                // ⚙️ Procesos Mongo → SOLO ADMIN
                .requestMatchers(HttpMethod.POST, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")

                // 💰 Finanzas
                .requestMatchers(HttpMethod.GET, "/api/finanzas/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")
                .requestMatchers(HttpMethod.POST, "/api/finanzas/pagos/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")

                // 🚨 Alertas
                .requestMatchers(HttpMethod.PUT, "/api/mongo/alertas/*/resolver")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")

                // 📦 Solicitudes
                .requestMatchers(HttpMethod.POST, "/api/mongo/solicitudes/nueva")
                    .hasAuthority("ROLE_USUARIO")

                // ✅ TODO LO DEMÁS REQUIERE TOKEN
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
