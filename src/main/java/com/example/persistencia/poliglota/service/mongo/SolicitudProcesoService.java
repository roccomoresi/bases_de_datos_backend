package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository repository;
    private final ProcesoRepository procesoRepository;

    public SolicitudProcesoService(SolicitudProcesoRepository repository, ProcesoRepository procesoRepository) {
        this.repository = repository;
        this.procesoRepository = procesoRepository;
    }

    public List<SolicitudProceso> getAll() {
        return repository.findAll();
    }

    public Optional<SolicitudProceso> getById(UUID id) {
        return repository.findById(id);
    }

    public List<SolicitudProceso> getByUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<SolicitudProceso> getByEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado);
    }

    public SolicitudProceso create(UUID usuarioId, UUID procesoId) {
        Proceso proceso = procesoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        return repository.save(solicitud);
    }

    public SolicitudProceso updateEstado(UUID id, String nuevoEstado) {
        return repository.findById(id).map(s -> {
            s.setEstado(nuevoEstado);
            return repository.save(s);
        }).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }

    public SolicitudProceso agregarResultado(UUID id, String resultado) {
        return repository.findById(id).map(s -> {
            s.setResultado(resultado);
            s.setEstado("completado");
            return repository.save(s);
        }).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
