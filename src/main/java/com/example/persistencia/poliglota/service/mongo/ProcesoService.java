package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.HistorialEjecucion;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcesoService {

    private final ProcesoRepository repository;
    private final HistorialEjecucionService historialService;

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
    public List<Proceso> getAll() {
        return repository.findAll();
    }

    public List<Proceso> getActivos() {
        return repository.findByActivoTrue();
    }

    /** Obtiene o lanza 404 */
    public Proceso obtenerPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado: " + id));
    }

    public List<Proceso> getByTipo(String tipo) {
        return repository.findByTipoIgnoreCase(tipo);
    }

    /* ───────── CREAR / GUARDAR ───────── */
    public Proceso save(Proceso proceso) {
        if (proceso.getId() == null || proceso.getId().isEmpty()) {
            proceso.setId(UUID.randomUUID().toString()); // ✅ genera String UUID
        }
        return repository.save(proceso);
    }

    /* ───────── ACTUALIZAR ─────────
       Nota: como 'activo' es primitive boolean (no puede ser null),
       por defecto NO lo tocamos acá; para eso dejamos toggleEstado().
     */
    public Proceso update(String id, Proceso updated) {
        Proceso p = obtenerPorId(id);

        if (updated.getNombre() != null)      p.setNombre(updated.getNombre());
        if (updated.getDescripcion() != null) p.setDescripcion(updated.getDescripcion());
        if (updated.getTipo() != null)        p.setTipo(updated.getTipo());
        if (updated.getCosto() != null)       p.setCosto(updated.getCosto());

        // Si también querés permitir actualizar 'activo' acá, descomentá:
        // p.setActivo(updated.isActivo());

        return repository.save(p);
    }

    /* ───────── ELIMINAR ───────── */
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proceso no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    /* ───────── ACTIVAR / DESACTIVAR ───────── */
    public Proceso toggleEstado(String id) {
        Proceso p = obtenerPorId(id);
        p.setActivo(!p.isActivo());
        return repository.save(p);
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
