package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso.EstadoProceso;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Comparator;
import java.util.List;

/**
 * Escucha eventos de pago de facturas (SQL)
 * y ejecuta automáticamente el proceso técnico en Mongo/Cassandra.
 *
 * Flujo:
 * 1️⃣ FacturaPagadaEvent (SQL)
 * 2️⃣ Buscar solicitud pendiente en Mongo
 * 3️⃣ Ejecutar proceso asociado (Cassandra)
 * 4️⃣ Completar y registrar historial
 */
@Component
public class ProcesoEjecucionHandler {

    private final SolicitudProcesoService solicitudService;
    private final ProcesoExecutorService executorService;

    public ProcesoEjecucionHandler(SolicitudProcesoService solicitudService,
                                   ProcesoExecutorService executorService) {
        this.solicitudService = solicitudService;
        this.executorService = executorService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFacturaPagada(FacturaPagadaEvent event) {
        Integer usuarioId = event.getUsuarioId();
        if (usuarioId == null) return;

        List<SolicitudProceso> pendientes = solicitudService.getByUsuario(usuarioId).stream()
                .filter(s -> s.getEstado() == EstadoProceso.PENDIENTE)
                .sorted(Comparator.comparing(SolicitudProceso::getFechaSolicitud))
                .toList();

        if (pendientes.isEmpty()) return;

        // ✅ Tomar la última solicitud pendiente
        SolicitudProceso solicitud = pendientes.get(pendientes.size() - 1);

        try {
            // 1️⃣ Marcar como EN_CURSO
            solicitudService.updateEstado(solicitud.getId(), EstadoProceso.EN_CURSO);

            // 2️⃣ Ejecutar proceso técnico
            executorService.ejecutarProceso(usuarioId, solicitud.getProceso().getId());

            // 3️⃣ Marcar como COMPLETADO
            solicitudService.updateEstado(solicitud.getId(), EstadoProceso.COMPLETADO);

            System.out.printf("✅ Proceso '%s' ejecutado tras pago de factura del usuario %d%n",
                    solicitud.getProceso().getNombre(), usuarioId);

        } catch (Exception ex) {
            System.err.println("⚠️ Error ejecutando proceso tras pago: " + ex.getMessage());
        }
    }
}
