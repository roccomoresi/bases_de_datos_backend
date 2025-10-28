package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.service.sql.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    /* ───────────────────────────────
       📋 LISTAR TODOS
    ─────────────────────────────── */
    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        log.info("📋 GET /api/sql/roles");
        return ResponseEntity.ok(rolService.listarTodos());
    }

    /* ───────────────────────────────
       🆕 CREAR ROL
    ─────────────────────────────── */
    @PostMapping
    public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
        try {
            Rol nuevo = rolService.crearRol(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            log.error("❌ Error al crear rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /* ───────────────────────────────
       ✏️ ACTUALIZAR ROL
    ─────────────────────────────── */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Rol rol) {
        try {
            Rol actualizado = rolService.actualizarRol(id, rol);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("❌ Error al actualizar rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /* ───────────────────────────────
       ❌ ELIMINAR ROL
    ─────────────────────────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        try {
            rolService.eliminarRol(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("❌ Error al eliminar rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
