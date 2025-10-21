package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import com.example.persistencia.poliglota.service.sql.SesionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql/sesiones")
public class SesionController {

    private final SesionService service;

    public SesionController(SesionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sesion> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Sesion create(@RequestBody Sesion sesion) {
        return service.save(sesion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
