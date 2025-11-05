package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.dto.SolicitudProcesoRequest;
import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import com.example.persistencia.poliglota.service.sql.FacturaService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import com.example.persistencia.poliglota.model.mongo.Proceso;
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

    public SolicitudProcesoService(SolicitudProcesoRepository repository,
                                   ProcesoRepository procesoRepository,
                                   HistorialEjecucionService historialService, FacturaService facturaService) {
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

    public List<SolicitudProceso> getByEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado);
    }



    /* ───────────────────────────────
       🟢 CREAR SOLICITUD
    ─────────────────────────────── */
    @Transactional
public SolicitudProceso create(Integer usuarioId, String procesoId) {
    Proceso proceso = procesoRepository.findById(procesoId)
            .orElseThrow(() -> new RuntimeException("❌ Proceso no encontrado con id: " + procesoId));

    // 🔹 Crear la solicitud en Mongo
    SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
    solicitud.setEstado("pendiente");
    solicitud.setResultado("A la espera de pago");

    repository.save(solicitud);

    // 🔹 Crear la factura pendiente en SQL
    try {
        facturaService.generarFacturaPendiente(
        usuarioId,
        "Solicitud del proceso: " + proceso.getNombre(),
        proceso.getCosto().doubleValue() // ✅ conversión segura de BigDecimal → Double
);

        System.out.println("✅ Factura pendiente generada para el proceso " + proceso.getNombre());
    } catch (Exception e) {
        System.err.println("⚠️ Error generando factura pendiente: " + e.getMessage());
    }

    return solicitud;
}


public void completarSolicitudYRegistrarHistorial(SolicitudProceso solicitud, String resultado) {
    solicitud.setEstado("completado");
    solicitud.setResultado(resultado);
    repository.save(solicitud);

    historialService.save(new HistorialEjecucion(
        solicitud.getProceso().getId(),
        solicitud.getProceso().getNombre(),
        solicitud.getUsuarioId(),
        solicitud.getFechaSolicitud(),
        LocalDateTime.now(),
        resultado
    ));
}




    /* ───────────────────────────────
       🔄 ACTUALIZAR ESTADO
    ─────────────────────────────── */
public SolicitudProceso updateEstado(UUID id, String estado) {
    SolicitudProceso solicitud = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("❌ Solicitud no encontrada con id: " + id));

    solicitud.setEstado(estado);
    repository.save(solicitud);

    // 🧩 Si el proceso se completó, registramos en el historial
    if ("completado".equalsIgnoreCase(estado) && solicitud.getProceso() != null) {
        String procesoId = solicitud.getProceso().getId(); // ✅ String
        String nombreProceso = solicitud.getProceso().getNombre();
        Integer usuarioId = solicitud.getUsuarioId();
        String resultado = solicitud.getResultado() != null ? solicitud.getResultado() : "Sin resultado";

        HistorialEjecucion log = new HistorialEjecucion(
                procesoId,                  // String
                nombreProceso,              // String
                usuarioId,                  // Integer
                solicitud.getFechaSolicitud(),
                LocalDateTime.now(),
                resultado
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

    // Validamos que el historial pertenezca al mismo usuario
    if (ultimo.getUsuarioId() != null &&
        ultimo.getUsuarioId().equals(solicitud.getUsuarioId())) {

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
