package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.repository.sql.SesionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SesionService {
    private final SesionRepository repository;

    public SesionService(SesionRepository repository) {
        this.repository = repository;
    }

    public List<Sesion> obtenerHistorialSesiones(Integer usuarioId) {
    return repository.findByUsuario_IdUsuario(usuarioId);
}


    public List<Sesion> getAll() {
        return repository.findAll();
    }

    public Sesion save(Sesion sesion) {
        return repository.save(sesion);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
