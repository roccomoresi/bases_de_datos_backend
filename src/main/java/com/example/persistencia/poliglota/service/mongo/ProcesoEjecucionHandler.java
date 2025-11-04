package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Comparator;
import java.util.List;

/**
 * Handler de eventos para ejecutar procesos técnicos en Mongo/Cassandra
 * cuando una factura asociada fue pagada en el dominio SQL.
 *
 * Desacopla los módulos evitando dependencias circulares:
 * SQL publica FacturaPagadaEvent → Mongo escucha AFTER_COMMIT y ejecuta.
 */
@Component
public class ProcesoEjecucionHandler {

    private final SolicitudProcesoService solicitudProcesoService;
    private final ProcesoService procesoService;

    public ProcesoEjecucionHandler(SolicitudProcesoService solicitudProcesoService,
                                   ProcesoService procesoService) {
        this.solicitudProcesoService = solicitudProcesoService;
        this.procesoService = procesoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFacturaPagada(FacturaPagadaEvent event) {
        Integer usuarioId = event.getUsuarioId();
        if (usuarioId == null) return;

        List<SolicitudProceso> pendientes = solicitudProcesoService.getByUsuario(usuarioId)
                .stream()
                .filter(s -> "pendiente".equalsIgnoreCase(s.getEstado()))
                .sorted(Comparator.comparing(SolicitudProceso::getFechaSolicitud))
                .toList();

        if (pendientes.isEmpty()) return;

        // Tomar la solicitud más reciente
        SolicitudProceso solicitud = pendientes.get(pendientes.size() - 1);

        try {
            // Marcar en progreso
            solicitudProcesoService.updateEstado(solicitud.getId(), "en_progreso");

            // Ejecutar proceso asociado
            if (solicitud.getProceso() != null) {
                procesoService.ejecutarProceso(solicitud.getProceso().getId());
            }

            // Completar y registrar historial
            solicitudProcesoService.updateEstado(solicitud.getId(), "completado");
        } catch (Exception ex) {
            // En esta versión solo logeamos a consola para no interrumpir el flujo de pago
            System.err.println("⚠️ Error ejecutando proceso tras pago: " + ex.getMessage());
        }
    }
}

