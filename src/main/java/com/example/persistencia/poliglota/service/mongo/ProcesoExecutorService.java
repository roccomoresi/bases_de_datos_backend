package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
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
     * Ejecuta el proceso (Mongo) y genera la factura (SQL).
     * Devuelve un Map<String,Object> para que el controller responda en JSON.
     */
    public Map<String, Object> ejecutarProceso(Integer usuarioId, String procesoId) {
        // 1) Buscar proceso
        Proceso proceso = procesoRepo.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        // 2) Crear solicitud
        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        solicitud.setEstado("en_progreso");
        solicitudRepo.save(solicitud);

        // 🟠 2. Ejecutar el proceso según tipo
        String resultado;
        LocalDateTime inicio = solicitud.getFechaSolicitud();
        LocalDateTime fin = LocalDateTime.now();

        switch (proceso.getTipo().toLowerCase()) {
            case "informe" -> resultado = generarInformePromedio();
            case "alerta" -> resultado = generarAlertas();
            case "servicio" -> resultado = ejecutarServicioBasico();
            default -> resultado = "✅ Proceso ejecutado sin acciones específicas.";
        }

        // 🔵 3. Guardar historial
        HistorialEjecucion log = new HistorialEjecucion(
                proceso.getId(),
                proceso.getNombre(),
                usuarioId,
                inicio,
                LocalDateTime.now(),
                resultado
        );
        historialService.save(log);

        // 🧾 4. Facturar el proceso como PENDIENTE (sin impacto contable inmediato)
        facturaService.generarFacturaPendiente(usuarioId, proceso.getNombre(), proceso.getCosto().doubleValue());

        // 🟣 5. Marcar solicitud como completada
        solicitud.setResultado(resultado);
        solicitud.setEstado("completado");
        solicitudRepo.save(solicitud);

        return resultado;
    }

    /* ───────────────────────────────
       🔹 Tipos de procesos
    ─────────────────────────────── */

    // Informe: ejemplo de promedio real en Cassandra
    private String generarInformePromedio() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        if (mediciones.isEmpty()) return "Sin datos en Cassandra para Argentina.";

        double promedioTemp = mediciones.stream()
                .mapToDouble(m -> m.getTemperatura() != null ? m.getTemperatura() : 0)
                .average()
                .orElse(0);

        double promedioHumedad = mediciones.stream()
                .mapToDouble(m -> m.getHumedad() != null ? m.getHumedad() : 0)
                .average()
                .orElse(0);

        return String.format(
                "🌎 Informe Climático - Argentina%nTemperatura promedio: %.2f°C%nHumedad promedio: %.2f%%",
                promedioTemp, promedioHumedad
        );
    }

    // Alerta: busca valores extremos
    private String generarAlertas() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        long alertas = mediciones.stream()
                .filter(m -> m.getTemperatura() != null && m.getTemperatura() > 40)
                .count();

        return alertas > 0
                ? "⚠️ Se detectaron " + alertas + " temperaturas extremas (>40°C) en Argentina."
                : "✅ No se detectaron alertas en el rango actual.";
    }

    // Servicio básico
    private String ejecutarServicioBasico() {
        return "🔧 Servicio de consulta ejecutado correctamente (sin resultados adicionales).";
    }
}
