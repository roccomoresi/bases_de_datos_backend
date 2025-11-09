package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.repository.mongo.HistorialEjecucionRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HistorialEjecucionService {

    private final HistorialEjecucionRepository repository;

    public HistorialEjecucionService(HistorialEjecucionRepository repository) {
        this.repository = repository;
    }

    // ───────────────────────────────────────────────
    // LISTAR HISTORIAL COMPLETO
    // ───────────────────────────────────────────────
    public List<HistorialEjecucion> getAll() {
        return repository.findAll();
    }

    // ───────────────────────────────────────────────
    // FILTROS DE CONSULTA
    // ───────────────────────────────────────────────

    // Por usuario
    public List<HistorialEjecucion> getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    // Por proceso
    public List<HistorialEjecucion> getByProceso(String procesoId) {
        return repository.findByProcesoId(procesoId);
    }

    // Por usuario + proceso (ordenado por fecha fin desc)
    public List<HistorialEjecucion> getByUsuarioYProceso(Integer usuarioId, String procesoId) {
        return repository.findByUsuarioIdAndProcesoIdOrderByFechaFinDesc(usuarioId, procesoId);
    }

    // Rango de fechas (usa query del repo)
    public List<HistorialEjecucion> getByRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaInicioBetween(desde, hasta);
    }

    // Top 5 recientes del usuario (para vista rápida)
    public List<HistorialEjecucion> getTop5ByUsuario(Integer usuarioId) {
        return repository.findTop5ByUsuarioIdOrderByFechaFinDesc(usuarioId);
    }

    // Últimos 10 globales (dashboard)
    public List<HistorialEjecucion> getUltimos10() {
        return repository.findTop10ByOrderByFechaFinDesc();
    }

    // Obtener por id (UUID)
    public HistorialEjecucion getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado"));
    }

    // ───────────────────────────────────────────────
    // REGISTRAR / ACTUALIZAR EJECUCIÓN (calcula duración)
    // ───────────────────────────────────────────────
    public HistorialEjecucion save(HistorialEjecucion ejecucion) {
        // calcular duración si hay inicio y fin válidos
        if (ejecucion.getFechaInicio() != null
                && ejecucion.getFechaFin() != null
                && !ejecucion.getFechaFin().isBefore(ejecucion.getFechaInicio())) {
            long duracion = Duration.between(
                    ejecucion.getFechaInicio(),
                    ejecucion.getFechaFin()
            ).toSeconds();
            ejecucion.setDuracionSegundos(duracion);
        } else {
            ejecucion.setDuracionSegundos(null);
        }

        HistorialEjecucion saved = repository.save(ejecucion);

        // Log en consola
        System.out.printf(
                "✔ Historial registrado — Proceso: %s | Usuario: %d | Estado: %s | Duración: %ds%n",
                saved.getNombreProceso(),
                saved.getUsuarioId(),
                resumenEstado(saved.getResultado()),
                saved.getDuracionSegundos() != null ? saved.getDuracionSegundos() : 0
        );

        return saved;
    }

    // Utilidad: mostrar estado resumido
    private String resumenEstado(String resultado) {
        if (resultado == null) return "sin resultado";
        String r = resultado.toLowerCase();
        if (r.contains("pendiente")) return "pendiente";
        if (r.contains("curso"))     return "en curso";
        if (r.contains("éxito") || r.contains("exito") || r.contains("completado")) return "completado";
        return "otro";
    }
}