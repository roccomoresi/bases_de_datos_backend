package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Ciudad;
import com.example.persistencia.poliglota.repository.mongo.CiudadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CiudadService {

    private final CiudadRepository ciudadRepository;

    public CiudadService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    // ✅ Obtener todas las ciudades
    public List<Ciudad> getAll() {
        return ciudadRepository.findAll();
    }

    // ✅ Obtener una ciudad por ID
    public Optional<Ciudad> getById(UUID id) {
        return ciudadRepository.findById(id);
    }

    // ✅ Crear o actualizar una ciudad
    public Ciudad save(Ciudad ciudad) {
        if (ciudad.getId() == null) {
            ciudad.setId(UUID.randomUUID());
        }
        return ciudadRepository.save(ciudad);
    }

    // ✅ Eliminar una ciudad por ID
    public void deleteById(UUID id) {
        if (!ciudadRepository.existsById(id)) {
            throw new RuntimeException("Ciudad no encontrada con ID: " + id);
        }
        ciudadRepository.deleteById(id);
    }
}
