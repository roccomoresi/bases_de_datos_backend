package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso.EstadoProceso;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 🔄 Maneja la ejecución automática de procesos tras el pago de facturas.
 * 
 * Flujo completo:
 * 1️⃣ Evento FacturaPagadaEvent (SQL)
 * 2️⃣ Busca la solicitud pendiente asociada en Mongo
 * 3️⃣ Cambia estado a EN_CURSO y registra historial
 * 4️⃣ Ejecuta el proceso técnico (Cassandra / simulación)
 * 5️⃣ Cambia estado a COMPLETADO y registra resultado final
 */
@Component
public class ProcesoEjecucionHandler {

    private final SolicitudProcesoService solicitudService;
    private final ProcesoExecutorService executorService;
    private final HistorialEjecucionService historialService;

    public ProcesoEjecucionHandler(
            SolicitudProcesoService solicitudService,
            ProcesoExecutorService executorService,
            HistorialEjecucionService historialService
    ) {
        this.solicitudService = solicitudService;
        this.executorService = executorService;
        this.historialService = historialService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFacturaPagada(FacturaPagadaEvent event) {
        Integer usuarioId = event.getUsuarioId();
        String procesoId = event.getProcesoId();

        if (usuarioId == null || procesoId == null) return;

        // Buscar solicitud pendiente más reciente del usuario y proceso
        List<SolicitudProceso> pendientes = solicitudService.getByUsuario(usuarioId).stream()
                .filter(s -> s.getEstado() == EstadoProceso.PENDIENTE
                        && s.getProceso() != null
                        && procesoId.equals(s.getProceso().getId()))
                .sorted(Comparator.comparing(SolicitudProceso::getFechaSolicitud))
                .toList();

        if (pendientes.isEmpty()) return;

        SolicitudProceso solicitud = pendientes.get(pendientes.size() - 1);

        try {
            // 1️⃣ Marcar como EN_CURSO
            solicitudService.updateEstado(solicitud.getId(), EstadoProceso.EN_CURSO);

            // 🧾 Registrar en historial el paso a EN_CURSO
            historialService.save(new HistorialEjecucion(
                    solicitud.getProceso().getId(),
                    solicitud.getProceso().getNombre(),
                    usuarioId,
                    solicitud.getFechaSolicitud(),
                    LocalDateTime.now(),
                    "Pago confirmado — proceso en curso"
            ));

            // 2️⃣ Ejecutar proceso técnico (Cassandra o simulación)
            String resultado = executorService.ejecutarProceso(usuarioId, solicitud.getProceso().getId());

            // 3️⃣ Guardar resultado y marcar como COMPLETADO
            solicitud.setResultado(resultado != null ? resultado : "Proceso ejecutado con éxito");
            solicitudService.updateEstado(solicitud.getId(), EstadoProceso.COMPLETADO);

            // 4️⃣ Registrar en historial la finalización
            historialService.save(new HistorialEjecucion(
                    solicitud.getProceso().getId(),
                    solicitud.getProceso().getNombre(),
                    usuarioId,
                    solicitud.getFechaSolicitud(),
                    LocalDateTime.now(),
                    solicitud.getResultado()
            ));

            System.out.printf(
                "✅ Proceso '%s' ejecutado y completado tras pago de factura del usuario %d%n",
                solicitud.getProceso().getNombre(),
                usuarioId
            );

        } catch (Exception ex) {
            System.err.println("⚠️ Error ejecutando proceso tras pago: " + ex.getMessage());
        }
    }
}
