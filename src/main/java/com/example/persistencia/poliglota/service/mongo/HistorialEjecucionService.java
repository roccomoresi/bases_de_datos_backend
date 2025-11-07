package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.repository.mongo.HistorialEjecucionRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialEjecucionService {

    private final HistorialEjecucionRepository repository;

    public HistorialEjecucionService(HistorialEjecucionRepository repository) {
        this.repository = repository;
    }

    /* ───────────────────────────────
       📋 LISTAR HISTORIAL COMPLETO
    ─────────────────────────────── */
    public List<HistorialEjecucion> getAll() {
        return repository.findAll();
    }

    /* ───────────────────────────────
       🔎 FILTROS DE CONSULTA
    ─────────────────────────────── */
    public List<HistorialEjecucion> getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<HistorialEjecucion> getByProceso(String procesoId) {
        return repository.findByProcesoId(procesoId);
    }

    public List<HistorialEjecucion> getByRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findAll().stream()
                .filter(h -> h.getFechaInicio().isAfter(desde) && h.getFechaFin().isBefore(hasta))
                .toList();
    }

    /* ───────────────────────────────
       💾 REGISTRAR EJECUCIÓN (CON DURACIÓN)
    ─────────────────────────────── */
    public HistorialEjecucion save(HistorialEjecucion ejecucion) {
        if (ejecucion.getFechaInicio() != null && ejecucion.getFechaFin() != null) {
            long duracion = Duration.between(ejecucion.getFechaInicio(), ejecucion.getFechaFin()).toSeconds();
            ejecucion.setDuracionSegundos(duracion);
        }

        HistorialEjecucion saved = repository.save(ejecucion);

        System.out.printf(
                "🧾 Historial registrado — Proceso: %s | Usuario: %d | Estado: %s | Duración: %ds%n",
                ejecucion.getNombreProceso(),
                ejecucion.getUsuarioId(),
                resumenEstado(ejecucion.getResultado()),
                ejecucion.getDuracionSegundos() != null ? ejecucion.getDuracionSegundos() : 0
        );

        return saved;
    }

    /* ───────────────────────────────
       🧠 UTILIDAD: mostrar resumen limpio
    ─────────────────────────────── */
    private String resumenEstado(String resultado) {
        if (resultado == null) return "sin resultado";
        if (resultado.toLowerCase().contains("pendiente")) return "pendiente";
        if (resultado.toLowerCase().contains("en curso")) return "en curso";
        if (resultado.toLowerCase().contains("éxito") || resultado.toLowerCase().contains("completado"))
            return "completado";
        return "otro";
    }
}
