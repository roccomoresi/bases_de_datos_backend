package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso.EstadoProceso;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Comparator;
import java.util.List;

@Component
public class ProcesoEjecucionHandler {

    private final SolicitudProcesoService solicitudService;
    private final ProcesoExecutorService executorService;

    public ProcesoEjecucionHandler(
            SolicitudProcesoService solicitudService,
            ProcesoExecutorService executorService
    ) {
        this.solicitudService = solicitudService;
        this.executorService = executorService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFacturaPagada(FacturaPagadaEvent event) {
        Integer usuarioId = event.getUsuarioId();
        String procesoId = event.getProcesoId();

        if (usuarioId == null || procesoId == null) return;

        // Buscar solicitud pendiente más reciente del usuario
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
            solicitudService.updateEstadoYResultado(
                    solicitud.getId(),
                    EstadoProceso.EN_CURSO,
                    "Pago confirmado — proceso en curso"
            );

            // 2️⃣ Ejecutar proceso técnico y obtener resultado textual
            String resultado = executorService.ejecutarProceso(usuarioId, procesoId);
            if (resultado == null || resultado.isBlank()) {
                resultado = "✅ Proceso ejecutado con éxito";
            }

            // 3️⃣ Marcar COMPLETADO + guardar resultado real
            solicitudService.updateEstadoYResultado(
                    solicitud.getId(),
                    EstadoProceso.COMPLETADO,
                    resultado
            );

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
