package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso.EstadoProceso;
import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcesoExecutorService {

    private final ProcesoRepository procesoRepo;
    private final SolicitudProcesoRepository solicitudRepo;
    private final HistorialEjecucionService historialService;
    private final MedicionService medicionService;
    private final FacturaService facturaService;

    public ProcesoExecutorService(
            ProcesoRepository procesoRepo,
            SolicitudProcesoRepository solicitudRepo,
            HistorialEjecucionService historialService,
            MedicionService medicionService,
            FacturaService facturaService
    ) {
        this.procesoRepo = procesoRepo;
        this.solicitudRepo = solicitudRepo;
        this.historialService = historialService;
        this.medicionService = medicionService;
        this.facturaService = facturaService;
    }

    /**
     * Ejecuta el proceso técnico (Mongo/Cassandra) asociado a un usuario.
     * Retorna un resumen para el controller o logs.
     */
    public Map<String, Object> ejecutarProceso(Integer usuarioId, String procesoId) {
        // 1️⃣ Buscar proceso
        Proceso proceso = procesoRepo.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        // 2️⃣ Crear solicitud en Mongo
        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        solicitud.setEstado(EstadoProceso.EN_CURSO);
        solicitudRepo.save(solicitud);

        String resultado;
        LocalDateTime inicio = solicitud.getFechaSolicitud();

        // 3️⃣ Ejecutar según tipo
        switch (proceso.getTipo().toLowerCase()) {
            case "informe" -> resultado = generarInformePromedio();
            case "alerta" -> resultado = generarAlertas();
            case "servicio" -> resultado = ejecutarServicioBasico();
            default -> resultado = "✅ Proceso ejecutado sin acciones específicas.";
        }

        // 4️⃣ Guardar historial
        HistorialEjecucion log = new HistorialEjecucion(
                proceso.getId(),
                proceso.getNombre(),
                usuarioId,
                inicio,
                LocalDateTime.now(),
                resultado
        );
        historialService.save(log);

        // 5️⃣ Marcar solicitud como COMPLETADA
        solicitud.setResultado(resultado);
        solicitud.setEstado(EstadoProceso.COMPLETADO);
        solicitudRepo.save(solicitud);

        // 6️⃣ Generar factura pendiente (solo si se ejecuta manualmente)
        try {
            facturaService.generarFacturaPendiente(
                usuarioId,
                "Ejecución manual del proceso: " + proceso.getNombre(),
                proceso.getCosto().doubleValue(),
                proceso.getId() // ✅ vincula la factura al proceso ejecutado
                );

        } catch (Exception e) {
            System.err.println("⚠️ Error generando factura: " + e.getMessage());
        }

        // 7️⃣ Respuesta JSON amigable
        Map<String, Object> resp = new HashMap<>();
        resp.put("procesoId", proceso.getId());
        resp.put("usuarioId", usuarioId);
        resp.put("estado", solicitud.getEstado());
        resp.put("resultado", resultado);
        return resp;
    }

    /* ───────────────────────────────
       🔹 Tipos de procesos técnicos
    ─────────────────────────────── */

    private String generarInformePromedio() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        if (mediciones.isEmpty()) return "Sin datos en Cassandra para Argentina.";

        double promedioTemp = mediciones.stream()
                .mapToDouble(m -> m.getTemperatura() != null ? m.getTemperatura() : 0)
                .average().orElse(0);

        double promedioHumedad = mediciones.stream()
                .mapToDouble(m -> m.getHumedad() != null ? m.getHumedad() : 0)
                .average().orElse(0);

        return String.format(
                "🌎 Informe Climático - Argentina%nTemperatura promedio: %.2f°C%nHumedad promedio: %.2f%%",
                promedioTemp, promedioHumedad
        );
    }

    private String generarAlertas() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        long alertas = mediciones.stream()
                .filter(m -> m.getTemperatura() != null && m.getTemperatura() > 40)
                .count();

        return alertas > 0
                ? "⚠️ Se detectaron " + alertas + " temperaturas extremas (>40°C) en Argentina."
                : "✅ No se detectaron alertas en el rango actual.";
    }

    private String ejecutarServicioBasico() {
        return "🔧 Servicio de consulta ejecutado correctamente (sin resultados adicionales).";
    }
}
