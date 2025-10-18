package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcesoService {

    private final ProcesoRepository procesoRepository;

    public ProcesoService(ProcesoRepository procesoRepository) {
        this.procesoRepository = procesoRepository;
    }

    // ✅ Obtener todos los procesos
    public List<Proceso> getAll() {
        return procesoRepository.findAll();
    }

    // ✅ Obtener proceso por ID
    public Optional<Proceso> getById(UUID id) {
        return procesoRepository.findById(id);
    }

    // ✅ Crear o actualizar proceso
    public Proceso save(Proceso proceso) {
        if (proceso.getId() == null) {
            proceso.setId(UUID.randomUUID());
        }
        return procesoRepository.save(proceso);
    }

    // ✅ Eliminar proceso por ID
    public void deleteById(UUID id) {
        if (!procesoRepository.existsById(id)) {
            throw new RuntimeException("Proceso no encontrado con ID: " + id);
        }
        procesoRepository.deleteById(id);
    }
}
