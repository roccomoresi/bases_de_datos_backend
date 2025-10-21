package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import com.example.persistencia.poliglota.service.sql.RolService;
import com.example.persistencia.poliglota.service.sql.SesionService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;

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
                "detalle", usuario.getRol()
            ));
        }

        Rol rolExistente = rolService.getById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(rolExistente);
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
    public Usuario update(@PathVariable Integer id, @RequestBody Usuario usuario) {
        usuario.setIdUsuario(id);
        return service.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
    Optional<Usuario> usuarioOpt = service.findByEmail(loginRequest.getEmail());

    if (usuarioOpt.isEmpty()) {
        return ResponseEntity.status(401).body("Email no registrado");
    }

    Usuario usuario = usuarioOpt.get();

    if (!usuario.getContrasena().equals(loginRequest.getContrasena())) {
        return ResponseEntity.status(401).body("Contraseña incorrecta");
    }

    // Crear sesión
    Sesion nuevaSesion = new Sesion();
    nuevaSesion.setUsuario(usuario);
    nuevaSesion.setRol(usuario.getRol().getDescripcion());
    sesionService.save(nuevaSesion);

    return ResponseEntity.ok(usuario);
}



@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody Usuario nuevoUsuario) {
    Optional<Usuario> existente = service.findByEmail(nuevoUsuario.getEmail());

    if (existente.isPresent()) {
        return ResponseEntity.status(409).body("El email ya está registrado");
    }

    Rol rolUsuario = rolService.getByDescripcion("usuario");
    nuevoUsuario.setRol(rolUsuario);

    Usuario creado = service.save(nuevoUsuario);

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
