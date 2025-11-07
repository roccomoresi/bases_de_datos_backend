package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso.EstadoProceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import jakarta.transaction.Transactional;
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
    private final FacturaService facturaService;

    public SolicitudProcesoService(
            SolicitudProcesoRepository repository,
            ProcesoRepository procesoRepository,
            HistorialEjecucionService historialService,
            FacturaService facturaService) {

        this.repository = repository;
        this.procesoRepository = procesoRepository;
        this.historialService = historialService;
        this.facturaService = facturaService;
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

    public List<SolicitudProceso> getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<SolicitudProceso> getByEstado(EstadoProceso estado) {
        return repository.findByEstado(estado);
    }

    /* ───────────────────────────────
       🟢 CREAR SOLICITUD
    ─────────────────────────────── */
    @Transactional
    public SolicitudProceso create(Integer usuarioId, String procesoId) {
        Proceso proceso = procesoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("❌ Proceso no encontrado con id: " + procesoId));

        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        solicitud.setEstado(EstadoProceso.PENDIENTE);
        solicitud.setResultado("A la espera de pago");
        repository.save(solicitud);

        try {
            facturaService.generarFacturaPendiente(
                    usuarioId,
                    "Solicitud del proceso: " + proceso.getNombre(),
                    proceso.getCosto().doubleValue(),
                    proceso.getId()
            );
            System.out.println("✅ Factura pendiente generada para el proceso " + proceso.getNombre());
        } catch (Exception e) {
            System.err.println("⚠️ Error generando factura pendiente: " + e.getMessage());
        }

        historialService.save(new HistorialEjecucion(
                proceso.getId(),
                proceso.getNombre(),
                usuarioId,
                solicitud.getFechaSolicitud(),
                LocalDateTime.now(),
                "Solicitud creada — pendiente de pago"
        ));

        return solicitud;
    }

    /* ───────────────────────────────
       🔄 ACTUALIZAR ESTADO Y RESULTADO
    ─────────────────────────────── */
    @Transactional
    public SolicitudProceso updateEstadoYResultado(UUID id, EstadoProceso nuevoEstado, String resultado) {
        SolicitudProceso solicitud = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Solicitud no encontrada con id: " + id));

        solicitud.setEstado(nuevoEstado);

        if (resultado != null && !resultado.isBlank()) {
            solicitud.setResultado(resultado);
        }

        solicitud.setFechaActualizacion(LocalDateTime.now());
        repository.save(solicitud);

        String mensajeHistorial = (resultado != null && !resultado.isBlank())
                ? resultado
                : "Estado actualizado a " + nuevoEstado.name();

        historialService.save(new HistorialEjecucion(
                solicitud.getProceso().getId(),
                solicitud.getProceso().getNombre(),
                solicitud.getUsuarioId(),
                solicitud.getFechaSolicitud(),
                LocalDateTime.now(),
                mensajeHistorial
        ));

        return solicitud;
    }

    /* ───────────────────────────────
       📝 AGREGAR RESULTADO FINAL
    ─────────────────────────────── */
    @Transactional
    public SolicitudProceso updateResultado(UUID id, String resultado) {
        SolicitudProceso solicitud = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setResultado(resultado);
        solicitud.setEstado(EstadoProceso.COMPLETADO);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        repository.save(solicitud);

        historialService.save(new HistorialEjecucion(
                solicitud.getProceso().getId(),
                solicitud.getProceso().getNombre(),
                solicitud.getUsuarioId(),
                solicitud.getFechaSolicitud(),
                LocalDateTime.now(),
                resultado
        ));

        return solicitud;
    }

    /* ───────────────────────────────
       🔄 SOLO CAMBIAR ESTADO
    ─────────────────────────────── */
    @Transactional
    public SolicitudProceso updateEstado(UUID id, EstadoProceso nuevoEstado) {
        return updateEstadoYResultado(id, nuevoEstado, null);
    }

    /* ───────────────────────────────
       ❌ ELIMINAR
    ─────────────────────────────── */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
