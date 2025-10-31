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
import java.util.UUID;

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

    public String ejecutarProceso(Integer usuarioId, String procesoId) {
        Proceso proceso = procesoRepo.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        // 🟢 1. Crear solicitud
        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        solicitud.setEstado("en_progreso");
        solicitudRepo.save(solicitud);

        // 🟠 2. Ejecutar proceso (según tipo)
        String resultado;
        switch (proceso.getTipo().toLowerCase()) {
            case "informe":
                resultado = generarInformeClimatico();
                break;
            case "alerta":
                resultado = "🔔 Se ejecutó la verificación de alertas.";
                break;
            case "servicio":
                resultado = "⚙️ Servicio ejecutado correctamente.";
                break;
            default:
                resultado = "✅ Proceso ejecutado sin acciones adicionales.";
        }

        // 🔵 3. Actualizar solicitud y registrar historial
        solicitud.setResultado(resultado);
        solicitud.setEstado("completado");
        solicitudRepo.save(solicitud);

        HistorialEjecucion log = new HistorialEjecucion(
    proceso.getId(),                // 🔹 ahora es String
    proceso.getNombre(),
    usuarioId,                      // 🔹 Integer (del usuario SQL)
    solicitud.getFechaSolicitud(),  // 🔹 fechaInicio
    LocalDateTime.now(),            // 🔹 fechaFin
    resultado                       // 🔹 texto del resultado del proceso
);
historialService.save(log);


        historialService.save(log);

        // 🧾 4. Generar factura SQL
        facturaService.generarFactura(
                usuarioId,
                proceso.getNombre(),
                proceso.getCosto().doubleValue()
        );

        return resultado;
    }

    private String generarInformeClimatico() {
        // 🔹 Ejemplo: leer Cassandra y calcular promedio de temperatura global
        var datos = medicionService.obtenerPorPais("Argentina");
        double promedio = datos.stream()
                .mapToDouble(m -> m.getTemperatura() != null ? m.getTemperatura() : 0)
                .average()
                .orElse(0);
        return "🌎 Temperatura promedio en Argentina: " + String.format("%.2f", promedio) + "°C";
    }
}
