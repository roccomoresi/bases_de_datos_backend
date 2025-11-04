package com.example.persistencia.poliglota.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 📂 PÚBLICOS
                .requestMatchers(
                    "/auth/**",
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/usuarios/register",
                    "/api/usuarios/login"
                ).permitAll()

                // 👤 PERFIL primero (para que no lo pise /api/sql/**)
                .requestMatchers("/api/sql/perfil/**").authenticated()

                // 🔒 ADMIN y sesiones
                .requestMatchers("/api/sql/roles/**", "/api/sql/sesiones/**").hasAuthority("ROLE_ADMIN")

                // ⚙️ Procesos → solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/mongo/procesos/**").hasAuthority("ROLE_ADMIN")

                // 💰 Finanzas críticas → ADMIN
                .requestMatchers(HttpMethod.POST, "/api/finanzas/facturas").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/finanzas/facturas/*/pagar").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/finanzas/movimientos").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/finanzas/movimientos/*").hasAuthority("ROLE_ADMIN")

                // 🚨 Alertas
                .requestMatchers(HttpMethod.PUT, "/api/mongo/alertas/*/resolver")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")
                .requestMatchers(HttpMethod.DELETE, "/api/mongo/alertas/*").hasAuthority("ROLE_ADMIN")

                // ⚙️ Ejecución de procesos
                .requestMatchers("/api/procesos/ejecutar")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")

                // 👥 Usuarios: solo ADMIN
                .requestMatchers("/api/sql/usuarios/**")
                    .hasAuthority("ROLE_ADMIN")

                // 📊 Lecturas de finanzas permitidas a USUARIO/ADMIN
                .requestMatchers(HttpMethod.GET, "/api/finanzas/facturas/*", "/api/finanzas/cuenta/*")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")
                // 💰 Resto del módulo finanzas → ADMIN
                .requestMatchers("/api/finanzas/**").hasAuthority("ROLE_ADMIN")

                // 📊 Módulo SQL general (excepto usuarios/roles/sesiones ya tratados)
                .requestMatchers("/api/sql/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_USUARIO")

                // 🧠 Monitoreo: ADMIN o TECNICO
                .requestMatchers("/api/monitoreo/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_TECNICO")

                // 📦 Informes y Mongo: autenticados
                .requestMatchers("/api/informes/**", "/api/mongo/**")
                    .authenticated()

                // 🔒 Default
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
