package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.repository.sql.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    public List<Rol> getAll() {
        return repository.findAll();
    }

    public Rol save(Rol rol) {
        return repository.save(rol);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Rol getByDescripcion(String descripcion) {
        return repository.findAll()
                .stream()
                .filter(r -> r.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElse(null);
    }

    // ✅ Nuevo método para buscar por ID
    public Optional<Rol> getById(Integer id) {
        return repository.findById(id);
    }
}
