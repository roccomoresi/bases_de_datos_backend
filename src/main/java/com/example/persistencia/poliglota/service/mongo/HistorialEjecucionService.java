package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.repository.mongo.HistorialEjecucionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialEjecucionService {

    private final HistorialEjecucionRepository repository;

    public HistorialEjecucionService(HistorialEjecucionRepository repository) {
        this.repository = repository;
    }

    /* ───────────────────────────────
       📋 LISTAR HISTORIAL
    ─────────────────────────────── */
    public List<HistorialEjecucion> getAll() {
        return repository.findAll();
    }

    public List<HistorialEjecucion> getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<HistorialEjecucion> getByProceso(String procesoId) {
        return repository.findByProcesoId(procesoId);
    }

    /* ───────────────────────────────
       💾 GUARDAR NUEVA EJECUCIÓN
    ─────────────────────────────── */
    public HistorialEjecucion save(HistorialEjecucion ejecucion) {
        return repository.save(ejecucion);
    }
}
