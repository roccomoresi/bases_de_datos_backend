package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.config.security.JwtService;
import com.example.persistencia.poliglota.dto.AuthResponse;
import com.example.persistencia.poliglota.dto.LoginRequest;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.SesionService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth", description = "Login / Register / Logout")
@Slf4j
@RestController
@RequestMapping ("/api/auth")

public class AuthController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder; // ✅ sin @Autowired, se inyecta por constructor
    private final SesionService sesionService;

    public AuthController(
            UsuarioService usuarioService,
            RolService rolService,
            CuentaCorrienteService cuentaCorrienteService,
            JwtService jwtService,
            PasswordEncoder encoder,
            SesionService sesionService
    ) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.cuentaCorrienteService = cuentaCorrienteService;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.sesionService = sesionService;
    }

    /* ───────────────────────────────
       🔐 LOGIN
    ─────────────────────────────── */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        Optional<Usuario> opt = usuarioService.buscarPorEmail(req.getEmail());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Email no registrado");
        }

        Usuario u = opt.get();

        boolean passwordOk;

        // ✅ Logs para depuración
        log.info("🔑 Contraseña ingresada: {}", req.getPassword());
        log.info("🔒 Hash almacenado: {}", u.getContrasena());

        if (u.getContrasena() != null && (
                u.getContrasena().startsWith("$2a$") ||
                u.getContrasena().startsWith("$2b$") ||
                u.getContrasena().startsWith("$2y$")
        )) {
            passwordOk = encoder.matches(req.getPassword(), u.getContrasena());
            log.info("🧮 BCrypt match: {}", passwordOk);
        } else {
            passwordOk = Objects.equals(req.getPassword(), u.getContrasena());
            log.info("🔍 Plain-text match: {}", passwordOk);
        }

        if (!passwordOk) {
            log.warn("❌ Contraseña incorrecta para usuario {}", req.getEmail());
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // ✅ Determinar rol
        String rolDesc = (u.getRol() != null ? u.getRol().getDescripcion() : "USUARIO");
        String springRole = switch (rolDesc.trim().toUpperCase()) {
            case "ADMIN", "ADMINISTRADOR" -> "ROLE_ADMIN";
            case "TECNICO", "TÉCNICO" -> "ROLE_TECNICO";
            default -> "ROLE_USUARIO";
        };

        Sesion sesion = sesionService.registrarInicioSesion(u.getIdUsuario(), rolDesc);
        String token = jwtService.generarToken(u);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("usuario", u);
        resp.put("idSesion", sesion.getIdSesion()); // 👈 nuevo campo

        log.info("✅ Login exitoso: {} ({}) [idSesion={}]", u.getEmail(), springRole, sesion.getIdSesion());
        return ResponseEntity.ok(resp);

    }

    /* ───────────────────────────────
       🚪 LOGOUT
    ─────────────────────────────── */
@PutMapping("/logout/{idSesion}")
public ResponseEntity<String> logout(@PathVariable Long idSesion) {  // 👈 cambiá a Long
    try {
        sesionService.cerrarSesion(idSesion); // 👈 también Long
        log.info("🚪 Sesión cerrada correctamente (ID: {})", idSesion);
        return ResponseEntity.ok("Sesión cerrada correctamente");
    } catch (Exception e) {
        log.error("❌ Error al cerrar sesión: {}", e.getMessage());
        return ResponseEntity.status(500).body("Error al cerrar la sesión");
    }
}


    /* ───────────────────────────────
       🧾 REGISTER
    ─────────────────────────────── */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario nuevo) {
        log.info("🆕 Registro de nuevo usuario: {}", nuevo.getEmail());

        if (usuarioService.buscarPorEmail(nuevo.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("El email ya está registrado");
        }

        Rol rolAsignado;
        if (nuevo.getRol() == null || nuevo.getRol().getIdRol() == null) {
            rolAsignado = rolService.buscarPorDescripcion("USUARIO")
                    .orElseThrow(() -> new RuntimeException("No se encontró el rol USUARIO"));
        } else {
            rolAsignado = rolService.buscarPorId(nuevo.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        }
        nuevo.setRol(rolAsignado);

        // 🔐 Encriptar contraseña antes de guardar
        nuevo.setContrasena(encoder.encode(nuevo.getContrasena()));

        Usuario creado = usuarioService.crearUsuario(nuevo);

        // 💰 La cuenta corriente se crea automáticamente (trigger SQL)

        String token = jwtService.generarToken(creado);


        log.info("✅ Usuario registrado correctamente: {}", nuevo.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, creado));
    }
}
