package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.model.sql.Usuario.EstadoUsuario;
import com.example.persistencia.poliglota.repository.sql.RolRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
    public List<Usuario> listarTodos() {
        log.info("🟢 Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        log.info("🔍 Buscando usuario con id: {}", id);
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        log.info("🔍 Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarPorRol(String rolDescripcion) {
    return usuarioRepository.findByRolDescripcionIgnoreCase(rolDescripcion);
}


    /* ───────────────────────────────
       🆕 CREAR USUARIO
    ─────────────────────────────── */
    public Usuario crearUsuario(Usuario usuario) {
        log.info("🟢 Creando nuevo usuario: {}", usuario.getEmail());

        usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Ya existe un usuario con ese email");
        });

        // Rol por defecto si no se asigna
        if (usuario.getRol() == null) {
            Rol rolDefault = rolRepository.findByDescripcion("USUARIO")
                    .orElseThrow(() -> new RuntimeException("No se encontró el rol USUARIO"));
            usuario.setRol(rolDefault);
        }

        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuarioRepository.save(usuario);
    }

    /* ───────────────────────────────
       ✏️ ACTUALIZAR USUARIO
    ─────────────────────────────── */
    public Usuario actualizarUsuario(Integer id, Usuario nuevosDatos) {
        log.info("🟡 Actualizando usuario con id {}", id);

        return usuarioRepository.findById(id).map(u -> {
            u.setNombreCompleto(nuevosDatos.getNombreCompleto());
            u.setEmail(nuevosDatos.getEmail());
            if (nuevosDatos.getContrasena() != null && !nuevosDatos.getContrasena().isBlank()) {
                u.setContrasena(nuevosDatos.getContrasena());
            }
            if (nuevosDatos.getRol() != null) {
                u.setRol(nuevosDatos.getRol());
            }
            return usuarioRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /* ───────────────────────────────
       🔄 CAMBIAR ESTADO
    ─────────────────────────────── */
    public Usuario cambiarEstado(Integer id, String nuevoEstado) {
        log.info("🟠 Cambiando estado de usuario con id {} a {}", id, nuevoEstado);

        return usuarioRepository.findById(id).map(u -> {
            try {
                EstadoUsuario estado = EstadoUsuario.valueOf(nuevoEstado.toUpperCase());
                u.setEstado(estado);
                return usuarioRepository.save(u);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido: " + nuevoEstado);
            }
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /* ───────────────────────────────
       🔁 CAMBIAR ROL
    ─────────────────────────────── */
    public Usuario cambiarRol(Integer id, String rolDescripcion) {
        log.info("🟣 Cambiando rol del usuario {} a {}", id, rolDescripcion);

        return usuarioRepository.findById(id).map(u -> {
            Rol nuevoRol = rolRepository.findByDescripcion(rolDescripcion.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolDescripcion));
            u.setRol(nuevoRol);
            return usuarioRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /* ───────────────────────────────
       ❌ ELIMINAR USUARIO
    ─────────────────────────────── */
    public void eliminarUsuario(Integer id) {
        log.info("🔴 Eliminando usuario con id {}", id);
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
