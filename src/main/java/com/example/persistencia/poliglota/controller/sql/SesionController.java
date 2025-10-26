package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.service.sql.SesionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql/sesiones")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService service) {
        this.sesionService = service;
    }

            @GetMapping("/usuarios/{id}/sesiones")
        public ResponseEntity<List<Sesion>> obtenerHistorial(@PathVariable Integer id) {
            List<Sesion> sesiones = sesionService.obtenerHistorialSesiones(id);
            return ResponseEntity.ok(sesiones);
        }
        

    @PostMapping("/iniciar/{usuarioId}")
    public ResponseEntity<Sesion> iniciarSesion(@PathVariable Integer usuarioId) {
        Sesion sesion = sesionService.registrarInicioSesion(usuarioId);
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
