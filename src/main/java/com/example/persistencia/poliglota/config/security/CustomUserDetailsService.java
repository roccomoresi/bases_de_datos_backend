package com.example.persistencia.poliglota.config.security;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private String toSpringRole(String descripcionRol) {
        if (descripcionRol == null) return "ROLE_USUARIO";
        String base = descripcionRol.trim().toUpperCase(); // "ADMINISTRADOR", "TECNICO", "USUARIO"
        if (base.startsWith("ADMIN")) return "ROLE_ADMIN";
        if (base.startsWith("TEC"))   return "ROLE_TECNICO";
        return "ROLE_USUARIO";
    }

 @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Normalizamos los roles a los que Spring usa internamente
        String rol = u.getRol().getDescripcion().trim().toUpperCase();

        String springRole;
        switch (rol) {
            case "ADMINISTRADOR":
                springRole = "ROLE_ADMIN";
                break;
            case "TECNICO":
                springRole = "ROLE_TECNICO";
                break;
            default:
                springRole = "ROLE_USUARIO";
                break;
        }

        return new User(
                u.getEmail(),
                u.getContrasena(),
                Set.of(new SimpleGrantedAuthority(springRole))
        );
    }
}
