package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    /* ───────────────────────────────
       📋 LISTAR Y BUSCAR
    ─────────────────────────────── */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("📋 GET /api/sql/usuarios");
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        log.info("🔍 GET /api/sql/usuarios/{}", id);
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado"));
    }

    /* ───────────────────────────────
       🆕 CREAR USUARIO
    ─────────────────────────────── */
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            log.info("🟢 POST /api/sql/usuarios - {}", usuario.getEmail());

            // Evitar duplicados
            if (usuarioService.buscarPorEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El email ya está registrado.");
            }

            // Asignar rol (por ID o por defecto)
            Rol rolAsignado = usuario.getRol() != null && usuario.getRol().getIdRol() != null
                    ? rolService.buscarPorId(usuario.getRol().getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"))
                    : rolService.buscarPorDescripcion("USUARIO")
                        .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
            usuario.setRol(rolAsignado);

            Usuario nuevo = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            log.error("❌ Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear usuario: " + e.getMessage());
        }
    }

    /* ───────────────────────────────
       ✏️ ACTUALIZAR USUARIO
    ─────────────────────────────── */
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            log.info("🟡 PUT /api/sql/usuarios/{}", id);
            Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("❌ Error al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar usuario: " + e.getMessage());
        }
    }

    /* ───────────────────────────────
       🔄 CAMBIAR ESTADO
    ─────────────────────────────── */
    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        try {
            log.info("🟠 PUT /api/sql/usuarios/{}/estado?nuevoEstado={}", id, nuevoEstado);
            Usuario actualizado = usuarioService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("❌ Error al cambiar estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cambiar estado: " + e.getMessage());
        }
    }

    /* ───────────────────────────────
       🔁 CAMBIAR ROL
    ─────────────────────────────── */
    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Integer id, @RequestParam String rol) {
        try {
            log.info("🟡 PATCH /api/sql/usuarios/{}/rol?rol={}", id, rol);
            Usuario actualizado = usuarioService.cambiarRol(id, rol);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("❌ Error al cambiar rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cambiar rol: " + e.getMessage());
        }
    }

    /* ───────────────────────────────
       ❌ ELIMINAR USUARIO
    ─────────────────────────────── */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            log.info("🔴 DELETE /api/sql/usuarios/{}", id);
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("❌ Error al eliminar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    /* ───────────────────────────────
        📋 LISTAR Y BUSCAR
    ────────────────────────────── */
    // ✅ AGREGAR ACÁ DENTRO DE /usuarios
    @GetMapping("/usuarios/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        log.info("🔍 GET /api/sql/usuarios/email/{}", email);
        return usuarioService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    /* ───────────────────────────────
       🧩 ROLES (para el front)
    ─────────────────────────────── */
    // @GetMapping("/roles")
    // public ResponseEntity<?> listarRoles() {
    //     log.info("📋 GET /api/sql/roles");
    //     try {
    //         List<Rol> roles = rolService.listarTodos();
    //         return ResponseEntity.ok(roles);
    //     } catch (Exception e) {
    //         log.error("❌ Error al listar roles: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Error al listar roles: " + e.getMessage());
    //     }
    // }
}
