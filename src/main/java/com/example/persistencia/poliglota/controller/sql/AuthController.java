package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.dto.AuthResponse;
import com.example.persistencia.poliglota.dto.LoginRequest;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.security.JwtService;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder; // si aún no usás BCrypt, lo podemos saltear

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

    // LOGIN: recibe JSON {email, password} y devuelve {token, usuario}
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    Optional<Usuario> opt = usuarioService.findByEmail(req.getEmail());
    if (!opt.isPresent())
        return ResponseEntity.status(401).body("Email no registrado");

    Usuario u = opt.get();

    // Si todavía guardás contraseñas planas, usá equals; si migrás a BCrypt, usá encoder.matches()
    boolean passwordOk;
    if (u.getContrasena() != null && (
            u.getContrasena().startsWith("$2a$") ||
            u.getContrasena().startsWith("$2b$") ||
            u.getContrasena().startsWith("$2y$")
    )) {
        passwordOk = encoder.matches(req.getPassword(), u.getContrasena());
    } else {
        passwordOk = Objects.equals(req.getPassword(), u.getContrasena());
    }

    if (!passwordOk)
        return ResponseEntity.status(401).body("Contraseña incorrecta");

    // Normalizamos el rol
    String rolDesc = (u.getRol() != null ? u.getRol().getDescripcion() : "USUARIO");
    String springRole = rolDesc == null ? "ROLE_USUARIO" :
            (rolDesc.trim().toUpperCase().startsWith("ADMIN") ? "ROLE_ADMIN" :
             rolDesc.trim().toUpperCase().startsWith("TEC")   ? "ROLE_TECNICO" :
                                                                 "ROLE_USUARIO");

    // Generamos el token JWT
    String token = jwtService.generarToken(u.getEmail(), springRole);

    // Devolvemos el token y el usuario
    return ResponseEntity.ok(new AuthResponse(token, u));
}


    // REGISTER: recibe Usuario (JSON) y crea rol por defecto + cuenta corriente
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario nuevo) {
        if (usuarioService.findByEmail(nuevo.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("El email ya está registrado");
        }

        // rol por defecto: "usuario"
        Rol rolUsuario = rolService.getByDescripcion("usuario");
        nuevo.setRol(rolUsuario);

        // si querés migrar a BCrypt:
        // nuevo.setContrasena(encoder.encode(nuevo.getContrasena()));

        Usuario creado = usuarioService.save(nuevo);

        CuentaCorriente cc = new CuentaCorriente();
        cc.setUsuario(creado);
        cc.setSaldoActual(0.0);
        cuentaCorrienteService.save(cc);

        // opcional: devolver token post-registro
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_USUARIO");
        String token = jwtService.generarToken(creado.getEmail(), "ROLE_USUARIO");

        return ResponseEntity.ok(new AuthResponse(token, creado));
    }
}
