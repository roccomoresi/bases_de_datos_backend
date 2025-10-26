package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.config.security.JwtService;
import com.example.persistencia.poliglota.dto.AuthResponse;
import com.example.persistencia.poliglota.dto.LoginRequest;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final JwtService jwtService;
    @Autowired
    private final PasswordEncoder encoder;


    public AuthController(
            UsuarioService usuarioService,
            RolService rolService,
            CuentaCorrienteService cuentaCorrienteService,
            JwtService jwtService,
            PasswordEncoder encoder
    ) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.cuentaCorrienteService = cuentaCorrienteService;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    /* ───────────────────────────────
       🔐 LOGIN
    ─────────────────────────────── */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        
            System.out.println("🟡 Email recibido: " + req.getEmail());
            System.out.println("🟡 Password recibido: " + req.getPassword());

        Optional<Usuario> opt = usuarioService.buscarPorEmail(req.getEmail());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Email no registrado");
        }

        Usuario u = opt.get();

        boolean passwordOk;




        if (u.getContrasena() != null && (
                u.getContrasena().startsWith("$2a$") ||
                u.getContrasena().startsWith("$2b$") ||
                u.getContrasena().startsWith("$2y$")
        )) {
            // BCrypt
            passwordOk = encoder.matches(req.getPassword(), u.getContrasena());
        } else {
            // Contraseña plana
            passwordOk = Objects.equals(req.getPassword(), u.getContrasena());
        }

        if (!passwordOk) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // Normalizamos el rol
        String rolDesc = (u.getRol() != null ? u.getRol().getDescripcion() : "USUARIO");
        String springRole = switch (rolDesc.trim().toUpperCase()) {
            case "ADMIN" -> "ROLE_ADMIN";
            case "TECNICO" -> "ROLE_TECNICO";
            default -> "ROLE_USUARIO";
        };

        // Generamos el token JWT
        String token = jwtService.generarToken(u.getEmail(), springRole);

        return ResponseEntity.ok(new AuthResponse(token, u));
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

    // 🧠 Si no se envía rol, usamos USUARIO por defecto
    Rol rolAsignado;
    if (nuevo.getRol() == null || nuevo.getRol().getIdRol() == null) {
        rolAsignado = rolService.buscarPorDescripcion("USUARIO")
                .orElseThrow(() -> new RuntimeException("No se encontró el rol USUARIO"));
    } else {
        rolAsignado = rolService.buscarPorId(nuevo.getRol().getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }
    nuevo.setRol(rolAsignado);

    // 🔐 Encriptamos contraseña
    nuevo.setContrasena(encoder.encode(nuevo.getContrasena()));

    // 🧱 Guardamos usuario
    Usuario creado = usuarioService.crearUsuario(nuevo);

    // 💰 Trigger SQL crea la cuenta corriente automáticamente

    // 🎟️ Generamos token con el rol correcto
    String token = jwtService.generarToken(
            creado.getEmail(),
            "ROLE_" + creado.getRol().getDescripcion().toUpperCase()
    );

    return ResponseEntity.ok(new AuthResponse(token, creado));
}


}
