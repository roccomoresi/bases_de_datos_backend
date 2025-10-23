package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.SesionService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;

import jakarta.transaction.Transactional; // Solo una importación necesaria

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sql/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final SesionService sesionService;
    private final RolService rolService;
    private final CuentaCorrienteService cuentaService;

    public UsuarioController(UsuarioService service, SesionService sesionService, RolService rolService,
            CuentaCorrienteService cuentaService) {
        this.service = service;
        this.sesionService = sesionService;
        this.rolService = rolService;
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public List<Usuario> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        Optional<Usuario> usuarioOpt = service.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Rol inválido o ausente",
                        "detalle", usuario.getRol()));
            }

            Rol rolExistente = rolService.getById(usuario.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            usuario.setRol(rolExistente);
            // El estado se seteará como 'activo' por defecto gracias a la inicialización en
            // la entidad
            Usuario nuevo = service.save(usuario);

            // Crear cuenta corriente
            CuentaCorriente cuenta = new CuentaCorriente();
            cuenta.setUsuario(nuevo);
            cuenta.setSaldoActual(0.0);
            cuentaService.save(cuenta);

            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Usuario usuarioRequest) {

        // 1. Buscar el usuario existente
        Optional<Usuario> optUsuario = service.getById(id);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioExistente = optUsuario.get();

        // 2. Actualizar campos simples
        usuarioExistente.setNombreCompleto(usuarioRequest.getNombreCompleto());
        usuarioExistente.setEmail(usuarioRequest.getEmail());
        // NOTA: No actualices la contraseña aquí, eso debería ser un endpoint separado.
        // NOTA: No actualizamos el estado aquí, para eso está el endpoint /estado

        // 3. Verificar y actualizar el ROL (La parte importante)
        if (usuarioRequest.getRol() != null && usuarioRequest.getRol().getIdRol() != null) {

            // 4. Buscar el NUEVO rol en la base de datos
            Optional<Rol> optRol = rolService.getById(usuarioRequest.getRol().getIdRol());

            if (optRol.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El Rol especificado no existe"));
            }

            // 5. Asignar el ROL (manejado) al usuario existente
            usuarioExistente.setRol(optRol.get());
        }

        // 6. Guardar el usuario actualizado
        Usuario actualizado = service.save(usuarioExistente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            // ✅ Primero eliminar la cuenta corriente del usuario
            cuentaService.deleteByUsuarioId(id);

            // Luego eliminar el usuario
            service.delete(id);

            return ResponseEntity.ok().body(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }

    /**
     * Endpoint para activar o desactivar un usuario.
     * Acepta un @RequestParam, ej: /api/sql/usuarios/1/estado?nuevoEstado=inactivo
     */
    @PutMapping("/{id}/estado")
public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, String> body) {
    System.out.println("🟡 BODY RECIBIDO: " + body);

    String nuevoEstado = body.get("nuevoEstado");
    if (nuevoEstado == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar un nuevo estado"));
    }

    Optional<Usuario> opt = service.getById(id);
    if (opt.isEmpty()) {
        return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
    }

    Usuario u = opt.get();
    try {
        Usuario.EstadoUsuario estadoEnum = Usuario.EstadoUsuario.valueOf(nuevoEstado.trim().toUpperCase());
        u.setEstado(estadoEnum);
        Usuario actualizado = service.save(u);
        return ResponseEntity.ok(actualizado);
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Map.of("error", "Estado inválido: " + nuevoEstado));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(Map.of("error", "Error interno", "detalle", e.getMessage()));
    }
}


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        Optional<Usuario> usuarioOpt = service.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Email no registrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Validar contraseña
        if (!usuario.getContrasena().equals(loginRequest.getContrasena())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // VALIDACIÓN DE ESTADO: Verificar si el usuario está activo
        if (usuario.getEstado() != Usuario.EstadoUsuario.ACTIVO) {
            return ResponseEntity.status(403).body("La cuenta de usuario está inactiva.");
        }

        // Crear sesión
        Sesion nuevaSesion = new Sesion();
        nuevaSesion.setUsuario(usuario);
        nuevaSesion.setRol(usuario.getRol().getDescripcion());
        sesionService.save(nuevaSesion);

        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * Asigna el rol 'usuario' por defecto.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario nuevoUsuario) {
        Optional<Usuario> existente = service.findByEmail(nuevoUsuario.getEmail());

        if (existente.isPresent()) {
            return ResponseEntity.status(409).body("El email ya está registrado");
        }

        // Asignar el rol de "usuario" por defecto
        // (Asegúrate que rolService.getByDescripcion("usuario") exista y devuelva un Rol)
        Rol rolUsuario = rolService.getByDescripcion("usuario");
        if (rolUsuario == null) {
            // Manejo de error si el rol "usuario" no está en la BD
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Configuración interna: Rol 'usuario' no encontrado."));
        }
        nuevoUsuario.setRol(rolUsuario);
        // El estado 'activo' se asigna por defecto en la entidad Usuario

        // Guardar el nuevo usuario
        Usuario creado = service.save(nuevoUsuario);

        // Crear su cuenta corriente
        CuentaCorriente cuenta = new CuentaCorriente();
        cuenta.setUsuario(creado);
        cuenta.setSaldoActual(0.0);
        cuentaService.save(cuenta);

        return ResponseEntity.ok(creado);
    }

    public UsuarioService getService() {
        return service;
    }

    public SesionService getSesionService() {
        return sesionService;
    }
}

