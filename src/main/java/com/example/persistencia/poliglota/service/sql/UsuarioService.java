package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> getAll() {
        return repository.findAll();
    }

    public Optional<Usuario> getById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        Usuario saved = repository.save(usuario);
        repository.flush(); // 👈 fuerza escritura inmediata en la BD
        return saved;
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

public Optional<Usuario> findByEmail(String email) {
    return repository.findByEmail(email);
}

}
