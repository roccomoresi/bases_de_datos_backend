package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.repository.sql.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
    public List<Rol> listarTodos() {
        log.info("📋 Listando todos los roles");
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Integer id) {
        log.info("🔍 Buscando rol con id {}", id);
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarPorDescripcion(String descripcion) {
        log.info("🔍 Buscando rol por descripción {}", descripcion);
        return rolRepository.findByDescripcion(descripcion);
    }

    /* ───────────────────────────────
       🆕 CREAR NUEVO ROL
    ─────────────────────────────── */
    public Rol crearRol(Rol rol) {
        log.info("🟢 Creando nuevo rol: {}", rol.getDescripcion());
        rolRepository.findByDescripcion(rol.getDescripcion()).ifPresent(r -> {
            throw new RuntimeException("Ya existe un rol con esa descripción");
        });
        return rolRepository.save(rol);
    }

    /* ───────────────────────────────
       ✏️ ACTUALIZAR ROL
    ─────────────────────────────── */
    public Rol actualizarRol(Integer id, Rol datos) {
        log.info("🟡 Actualizando rol con id {}", id);
        return rolRepository.findById(id).map(r -> {
            r.setDescripcion(datos.getDescripcion());
            return rolRepository.save(r);
        }).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    /* ───────────────────────────────
       ❌ ELIMINAR ROL
    ─────────────────────────────── */
    public void eliminarRol(Integer id) {
        log.info("🔴 Eliminando rol con id {}", id);
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado");
        }
        rolRepository.deleteById(id);
    }
}
