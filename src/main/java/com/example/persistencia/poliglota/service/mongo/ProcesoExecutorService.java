package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.service.cassandra.MedicionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class ProcesoExecutorService {

    private final ProcesoRepository procesoRepo;
    private final HistorialEjecucionService historialService;
    private final MedicionService medicionService;

    public ProcesoExecutorService(
            ProcesoRepository procesoRepo,
            HistorialEjecucionService historialService,
            MedicionService medicionService
    ) {
        this.procesoRepo = procesoRepo;
        this.historialService = historialService;
        this.medicionService = medicionService;
    }

    /**
     * Ejecuta el proceso técnico asociado a un usuario.
     */
    public String ejecutarProceso(Integer usuarioId, String procesoId) {
        Proceso proceso = procesoRepo.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("❌ Proceso no encontrado con id: " + procesoId));

        String resultado;
        LocalDateTime inicio = LocalDateTime.now();

        switch (proceso.getTipo().toLowerCase()) {
            case "informe" -> resultado = generarInformePromedio();
            case "alerta" -> resultado = generarAlertas();
            case "analisis" -> resultado = generarAnalisisMensual();
            case "prediccion" -> resultado = generarPrediccion();
            case "servicio" -> resultado = ejecutarServicioBasico();
            default -> resultado = "✅ Proceso ejecutado sin acciones específicas.";
        }

        historialService.save(new HistorialEjecucion(
                proceso.getId(),
                proceso.getNombre(),
                usuarioId,
                inicio,
                LocalDateTime.now(),
                resultado
        ));

        return resultado;
    }

    /* ───────────────────────────────
       🔹 Tipos de procesos técnicos
    ─────────────────────────────── */

    /** 🌎 Informe de promedios climáticos por país */
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
                "🌎 Informe Climático Diario%nTemperatura promedio: %.2f °C%nHumedad promedio: %.2f %%",
                promedioTemp, promedioHumedad
        );
    }

    /** ⚠️ Detección de alertas de temperatura extrema */
    private String generarAlertas() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        long alertas = mediciones.stream()
                .filter(m -> m.getTemperatura() != null && m.getTemperatura() > 40)
                .count();

        return alertas > 0
                ? "⚠️ Se detectaron " + alertas + " temperaturas extremas (> 40 °C) en Argentina."
                : "✅ No se detectaron alertas en el rango actual.";
    }

    /** 📈 Análisis mensual de variación de humedad */
    private String generarAnalisisMensual() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        if (mediciones.isEmpty()) return "Sin datos en Cassandra para análisis mensual.";

        double promedioHumedad = mediciones.stream()
                .mapToDouble(m -> m.getHumedad() != null ? m.getHumedad() : 0)
                .average().orElse(0);

        double maxHumedad = mediciones.stream()
                .mapToDouble(m -> m.getHumedad() != null ? m.getHumedad() : 0)
                .max().orElse(0);

        double minHumedad = mediciones.stream()
                .mapToDouble(m -> m.getHumedad() != null ? m.getHumedad() : 0)
                .min().orElse(0);

        double variacion = maxHumedad - minHumedad;

        String mesActual = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "AR"));

        return String.format(
                "📊 Análisis de Humedad — %s%nPromedio: %.2f %% | Máx: %.2f %% | Mín: %.2f %% | Variación: %.2f %%",
                mesActual, promedioHumedad, maxHumedad, minHumedad, variacion
        );
    }

    /** 🤖 Predicción simple basada en tendencia de temperaturas */
    private String generarPrediccion() {
        var mediciones = medicionService.obtenerPorPais("Argentina");
        if (mediciones.isEmpty()) return "Sin datos en Cassandra para predicción.";

        List<Double> temps = mediciones.stream()
                .filter(m -> m.getTemperatura() != null)
                .map(m -> m.getTemperatura())
                .toList();

        double actual = temps.get(temps.size() - 1);
        double promedio = temps.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double tendencia = actual - promedio;

        String pronostico;
        if (tendencia > 3)
            pronostico = "☀️ Tendencia al alza — posible jornada más calurosa mañana.";
        else if (tendencia < -3)
            pronostico = "🌧️ Tendencia a la baja — posible descenso de temperatura.";
        else
            pronostico = "⛅ Temperaturas estables previstas.";

        return String.format(
                "🤖 Predicción Meteorológica:%nTemperatura actual: %.2f °C%nPromedio histórico: %.2f °C%n%s",
                actual, promedio, pronostico
        );
    }

    /** 🛠️ Servicio genérico sin lectura de datos */
    private String ejecutarServicioBasico() {
        return "🔧 Servicio de consulta ejecutado correctamente (sin resultados adicionales).";
    }
}
