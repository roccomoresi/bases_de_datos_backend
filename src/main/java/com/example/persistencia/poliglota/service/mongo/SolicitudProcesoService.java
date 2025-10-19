package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
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
    private final HistorialEjecucionService historialService;



    public SolicitudProcesoService(SolicitudProcesoRepository repository, ProcesoRepository procesoRepository,
            HistorialEjecucionService historialService) {
        this.repository = repository;
        this.procesoRepository = procesoRepository;
        this.historialService = historialService;
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
    SolicitudProceso solicitud = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

    solicitud.setEstado(nuevoEstado);
    repository.save(solicitud);

    // 🔹 Si el proceso fue completado, registramos historial
    if ("completado".equalsIgnoreCase(nuevoEstado)) {
        HistorialEjecucion log = new HistorialEjecucion(
                solicitud.getProceso().getId(),
                solicitud.getProceso().getNombre(),
                solicitud.getUsuarioId(),
                solicitud.getFechaSolicitud(),
                java.time.LocalDateTime.now(),
                solicitud.getResultado()
        );
        historialService.save(log);
        System.out.println("📜 Historial registrado para el proceso: " + solicitud.getProceso().getNombre());
    }

    return solicitud;
}


public SolicitudProceso agregarResultado(UUID id, String resultado) {
    SolicitudProceso solicitud = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

    solicitud.setResultado(resultado);
    solicitud.setEstado("completado");
    repository.save(solicitud);

    // 🔹 Sincronizar resultado en historial (si existe un log reciente)
    List<HistorialEjecucion> historial = historialService.getByProceso(solicitud.getProceso().getId());
    if (!historial.isEmpty()) {
        HistorialEjecucion ultimo = historial.get(historial.size() - 1);
        // solo si el historial pertenece al mismo usuario
        if (ultimo.getUsuarioId().equals(solicitud.getUsuarioId())) {
            ultimo.setResultado(resultado);
            historialService.save(ultimo);
            System.out.println("📝 Resultado actualizado también en historial: " + resultado);
        }
    }

    return solicitud;
}


    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
