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

        // 3) Ejecutar lógica según el tipo de proceso
        String resultado;
        String tipo = proceso.getTipo() != null ? proceso.getTipo().toLowerCase() : "";
        switch (tipo) {
            case "informe":
                resultado = generarInformeClimatico();
                break;
            case "alerta":
                resultado = "Se ejecutó la verificación de alertas.";
                break;
            case "servicio":
                resultado = "Servicio ejecutado correctamente.";
                break;
            default:
                resultado = "Proceso ejecutado sin acciones adicionales.";
        }

        // 4) Actualizar solicitud y registrar historial
        solicitud.setResultado(resultado);
        solicitud.setEstado("completado");
        solicitudRepo.save(solicitud);

        HistorialEjecucion log = new HistorialEjecucion(
                proceso.getId(),               // id del proceso (String)
                proceso.getNombre(),
                usuarioId,                     // usuario SQL (Integer)
                solicitud.getFechaSolicitud(), // inicio
                LocalDateTime.now(),           // fin
                resultado
        );
        historialService.save(log); // (una sola vez)

        // 5) Generar factura en SQL (null-safe)
        double monto = proceso.getCosto() != null ? proceso.getCosto() : 0.0;
        facturaService.generarFactura(
                usuarioId,
                proceso.getNombre(),
                monto
        );

        // 6) Armar respuesta
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "OK");
        resp.put("mensaje", "Proceso ejecutado y factura generada correctamente");
        resp.put("usuarioId", usuarioId);
        resp.put("procesoId", procesoId);
        resp.put("nombreProceso", proceso.getNombre());
        resp.put("montoFacturado", monto);
        resp.put("resultado", resultado);
        resp.put("solicitudId", solicitud.getId()); // si tu SolicitudProceso tiene id
        resp.put("timestamp", LocalDateTime.now());
        return resp;
    }

    private String generarInformeClimatico() {
        var datos = medicionService.obtenerPorPais("Argentina");
        double promedio = datos.stream()
                .mapToDouble(m -> m.getTemperatura() != null ? m.getTemperatura() : 0)
                .average()
                .orElse(0);
        return "Temperatura promedio en Argentina: " + String.format("%.2f", promedio) + "°C";
    }
}
