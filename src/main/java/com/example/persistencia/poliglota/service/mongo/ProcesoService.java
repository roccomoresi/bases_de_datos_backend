package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcesoService {

    private final ProcesoRepository repository;

    public ProcesoService(ProcesoRepository repository) {
        this.repository = repository;
    }

    public List<Proceso> getAll() {
        return repository.findAll();
    }

    public List<Proceso> getActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Proceso> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Proceso> getByTipo(String tipo) {
        return repository.findByTipoIgnoreCase(tipo);
    }

    public Proceso save(Proceso proceso) {
        if (proceso.getId() == null) {
            proceso.setId(UUID.randomUUID());
        }
        return repository.save(proceso);
    }

    public Proceso update(UUID id, Proceso updated) {
        return repository.findById(id).map(p -> {
            p.setNombre(updated.getNombre());
            p.setDescripcion(updated.getDescripcion());
            p.setTipo(updated.getTipo());
            p.setCosto(updated.getCosto());
            p.setActivo(updated.isActivo());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Proceso toggleEstado(UUID id) {
        return repository.findById(id).map(p -> {
            p.setActivo(!p.isActivo());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }
}
