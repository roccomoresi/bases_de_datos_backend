package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Obtener todos los usuarios
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    // ✅ Buscar usuario por ID
    public Optional<Usuario> getById(UUID id) {
        return usuarioRepository.findById(id);
    }

    // ✅ Crear o actualizar usuario
    public Usuario save(Usuario usuario) {
        // Si no tiene ID (nuevo usuario), le asignamos uno
        if (usuario.getId() == null) {
            usuario.setId(UUID.randomUUID());
        }
        return usuarioRepository.save(usuario);
    }

    // ✅ Eliminar usuario por ID
    public void delete(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
