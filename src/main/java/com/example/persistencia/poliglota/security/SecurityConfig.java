package com.example.persistencia.poliglota.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) // ✅ habilita CORS real
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (auth)
                .requestMatchers("/auth/**").permitAll()

                // -------- SQL (facturación / usuarios) --------
                .requestMatchers("/api/sql/usuarios/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(
                    "/api/sql/facturas/**",
                    "/api/sql/pagos/**",
                    "/api/sql/cuentas/**",
                    "/api/sql/movimientos/**"
                ).hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")

                // -------- Cassandra --------
                .requestMatchers("/api/cassandra/sensores/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")
                .requestMatchers("/api/mongo/alertas").permitAll()
                .requestMatchers("/api/cassandra/mediciones/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")


                // -------- Monitoreo --------
                .requestMatchers("/api/monitoreo/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")

                // -------- Mongo --------
                .requestMatchers("/api/mongo/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO", "ROLE_USUARIO")

                // -------- Informes --------
                .requestMatchers("/api/informes/**").authenticated()

                // Default
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
