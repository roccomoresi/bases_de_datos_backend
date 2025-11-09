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

    // ===== LISTAR / OBTENER =====
    public List<HistorialEjecucion> getAll() {
        return repository.findAll();
    }

    public HistorialEjecucion getById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Historial no encontrado"));
    }

    // ===== FILTROS =====
    public List<HistorialEjecucion> getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<HistorialEjecucion> getByProceso(String procesoId) {
        return repository.findByProcesoId(procesoId);
    }

    public List<HistorialEjecucion> getByUsuarioYProceso(Integer usuarioId, String procesoId) {
        return repository.findByUsuarioIdAndProcesoIdOrderByFechaFinDesc(usuarioId, procesoId);
    }

    public List<HistorialEjecucion> getByRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaInicioBetween(desde, hasta);
    }

    public List<HistorialEjecucion> getTop5ByUsuario(Integer usuarioId) {
        return repository.findTop5ByUsuarioIdOrderByFechaFinDesc(usuarioId);
    }

    public List<HistorialEjecucion> getUltimos10() {
        return repository.findTop10ByOrderByFechaFinDesc();
    }

    // ===== GUARDAR (recalcula duración) =====
    public HistorialEjecucion save(HistorialEjecucion ejecucion) {
        if (ejecucion.getFechaInicio() != null &&
                ejecucion.getFechaFin() != null &&
                !ejecucion.getFechaFin().isBefore(ejecucion.getFechaInicio())) {

            long duracion = Duration
                    .between(ejecucion.getFechaInicio(), ejecucion.getFechaFin())
                    .toSeconds();
            ejecucion.setDuracionSegundos(duracion);
        } else {
            ejecucion.setDuracionSegundos(null);
        }

        return repository.save(ejecucion);
    }

    // ===== VALIDACIÓN =====
    public List<HistorialEjecucion> getPendientesValidacion() {
        return repository.findByValidadoFalseOrderByFechaFinDesc();
    }

    public List<HistorialEjecucion> getValidados() {
        return repository.findByValidadoTrueOrderByFechaValidacionDesc();
    }

    public HistorialEjecucion validar(String id, String validador, String observaciones) {
        HistorialEjecucion h = getById(id);
        h.setValidado(true);
        h.setValidadoPor(validador);
        h.setFechaValidacion(LocalDateTime.now());
        h.setObservacionesValidacion(observaciones);
        return repository.save(h);
    }

    public HistorialEjecucion desvalidar(String id) {
        HistorialEjecucion h = getById(id);
        h.setValidado(false);
        h.setValidadoPor(null);
        h.setFechaValidacion(null);
        h.setObservacionesValidacion(null);
        return repository.save(h);
    }
}