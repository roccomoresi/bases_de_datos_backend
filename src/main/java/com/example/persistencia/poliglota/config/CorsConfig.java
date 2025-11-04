package com.example.persistencia.poliglota.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 🔹 Permití el front en Vite
                        .allowedOrigins("http://localhost:5173")
                        // 🔹 Métodos HTTP habilitados
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        // 🔹 Headers permitidos
                        .allowedHeaders("*")
                        // 🔹 Permite enviar cookies o auth headers si los usás
                        .allowCredentials(true)
                        // 🔹 Expira el preflight (OPTIONS) después de 1 hora
                        .maxAge(3600);
            }
        };
    }
}
