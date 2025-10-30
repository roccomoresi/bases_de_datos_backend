package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository repository;
    private final ProcesoRepository procesoRepository;
    private final HistorialEjecucionService historialService;

    public SolicitudProcesoService(SolicitudProcesoRepository repository,
                                   ProcesoRepository procesoRepository,
                                   HistorialEjecucionService historialService) {
        this.repository = repository;
        this.procesoRepository = procesoRepository;
        this.historialService = historialService;
    }

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
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

    /* ───────────────────────────────
       🟢 CREAR SOLICITUD
    ─────────────────────────────── */
    public SolicitudProceso create(Integer usuarioId, UUID procesoId) {
        Proceso proceso = procesoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        return repository.save(solicitud);
    }

    /* ───────────────────────────────
       🔄 ACTUALIZAR ESTADO
    ─────────────────────────────── */
    public SolicitudProceso updateEstado(UUID id, String estado) {
        SolicitudProceso solicitud = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(estado);
        repository.save(solicitud);

        // Si el proceso se completó, registramos en el historial
        if ("completado".equalsIgnoreCase(estado)) {
            HistorialEjecucion log = new HistorialEjecucion(
                    solicitud.getProceso().getId(),
                    solicitud.getProceso().getNombre(),
                    solicitud.getUsuarioId(),
                    solicitud.getFechaSolicitud(),
                    LocalDateTime.now(),
                    solicitud.getResultado()
            );
            historialService.save(log);
        }

        return solicitud;
    }

    /* ───────────────────────────────
       📝 AGREGAR RESULTADO
    ─────────────────────────────── */
    public SolicitudProceso updateResultado(UUID id, String resultado) {
        SolicitudProceso solicitud = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setResultado(resultado);
        solicitud.setEstado("completado");
        repository.save(solicitud);

        // Actualizar historial más reciente
        List<HistorialEjecucion> historial = historialService.getByProceso(solicitud.getProceso().getId());
        if (!historial.isEmpty()) {
            HistorialEjecucion ultimo = historial.get(historial.size() - 1);
            if (ultimo.getUsuarioId().equals(solicitud.getUsuarioId())) {
                ultimo.setResultado(resultado);
                historialService.save(ultimo);
            }
        }

        return solicitud;
    }

    /* ───────────────────────────────
       ❌ ELIMINAR
    ─────────────────────────────── */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
