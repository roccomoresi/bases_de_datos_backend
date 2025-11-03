package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcesoService {

    private final ProcesoRepository repository;
    private final HistorialEjecucionService historialService;

    public ProcesoService(ProcesoRepository repository, HistorialEjecucionService historialService) {
        this.repository = repository;
        this.historialService = historialService;
    }

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
    public List<Proceso> getAll() {
        return repository.findAll();
    }

    public List<Proceso> getActivos() {
        return repository.findByActivoTrue();
    }

    public Optional<Proceso> getById(String id) {
        return repository.findById(id);
    }

    public List<Proceso> getByTipo(String tipo) {
        return repository.findByTipoIgnoreCase(tipo);
    }

    /* ───────────────────────────────
       💾 CREAR O GUARDAR
    ─────────────────────────────── */
    public Proceso save(Proceso proceso) {
        if (proceso.getId() == null || proceso.getId().isEmpty()) {
            proceso.setId(UUID.randomUUID().toString()); // ✅ genera String UUID
        }
        return repository.save(proceso);
    }

    /* ───────────────────────────────
       ✏️ ACTUALIZAR
    ─────────────────────────────── */
    public Proceso update(String id, Proceso updated) {
        return repository.findById(id).map(p -> {
            p.setNombre(updated.getNombre());
            p.setDescripcion(updated.getDescripcion());
            p.setTipo(updated.getTipo());
            p.setCosto(updated.getCosto());
            p.setActivo(updated.isActivo());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }

    /* ───────────────────────────────
       ❌ ELIMINAR
    ─────────────────────────────── */
    public void delete(String id) {
        repository.deleteById(id);
    }

    /* ───────────────────────────────
       🔄 ACTIVAR / DESACTIVAR
    ─────────────────────────────── */
    public Proceso toggleEstado(String id) {
        return repository.findById(id).map(p -> {
            p.setActivo(!p.isActivo());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }

    /* ───────────────────────────────
       ⚙️ EJECUTAR PROCESO (al pagar)
    ─────────────────────────────── */
    public void ejecutarProceso(String procesoId) {
        Proceso proceso = repository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("❌ Proceso no encontrado: " + procesoId));

        // ⚙️ Simulación de ejecución (en tu caso, podrías conectar con Cassandra)
        String resultado = "Ejecución automática del proceso: " + proceso.getNombre();

        // 🕓 Registrar en el historial de ejecuciones
        HistorialEjecucion log = new HistorialEjecucion();
        log.setProcesoId(proceso.getId());
        log.setNombreProceso(proceso.getNombre());
        log.setUsuarioId(null); // si querés, podés pasar el usuario
        log.setFechaInicio(LocalDateTime.now());
        log.setFechaFin(LocalDateTime.now());
        log.setResultado(resultado);

        historialService.save(log);

        System.out.println("✅ Proceso ejecutado automáticamente: " + proceso.getNombre());
    }
}
