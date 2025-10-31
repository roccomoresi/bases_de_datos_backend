package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.service.sql.SesionService;
import com.example.persistencia.poliglota.service.sql.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/sesiones")
public class SesionController {

    private final SesionService sesionService;

    @Autowired
    private final UsuarioService usuarioService;

    

            public SesionController(SesionService sesionService, UsuarioService usuarioService) {
        this.sesionService = sesionService;
        this.usuarioService = usuarioService;
    }

            @GetMapping("/usuarios/{id}/sesiones")
        public ResponseEntity<List<Sesion>> obtenerHistorial(@PathVariable Integer id) {
            List<Sesion> sesiones = sesionService.obtenerHistorialSesiones(id);
            return ResponseEntity.ok(sesiones);
        }
        


            @PostMapping("/iniciar/{usuarioId}")
            public ResponseEntity<Sesion> iniciarSesion(@PathVariable Integer usuarioId) {
                Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                String rol = usuario.getRol().getDescripcion();
                Sesion sesion = sesionService.registrarInicioSesion(usuarioId, rol);
                return ResponseEntity.ok(sesion);
            }



        /* 🚪 PUT cerrar sesión */
    @PutMapping("/cerrar/{idSesion}")
    public ResponseEntity<String> cerrarSesion(@PathVariable Integer idSesion) {
        sesionService.cerrarSesion(idSesion);
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    @GetMapping
    public List<Sesion> getAll() {
        return sesionService.getAll();
    }

    @PostMapping
    public Sesion create(@RequestBody Sesion sesion) {
        return sesionService.save(sesion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        sesionService.delete(id);
    }
}
