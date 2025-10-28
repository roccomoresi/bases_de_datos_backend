package com.example.persistencia.poliglota.config.security;

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
        .cors(Customizer.withDefaults()) // Habilita CORS
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // ✅ Endpoints públicos
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/sql/auth/**",
                "/api/sql/usuarios/register",
                "/api/sql/usuarios/login",
                "/api/cassandra/**",
                "/api/mongo/**"
            ).permitAll()

            // 🔐 Rutas protegidas
            .requestMatchers("/api/sql/usuarios/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/api/sql/facturas/**", "/api/sql/pagos/**", "/api/sql/cuentas/**", "/api/sql/movimientos/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")
            .requestMatchers("/api/monitoreo/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")
            .requestMatchers("/api/informes/**").authenticated()

            // 🔒 Por defecto, todo lo demás requiere autenticación
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
public PasswordEncoder passwordEncoder() {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
}

}
