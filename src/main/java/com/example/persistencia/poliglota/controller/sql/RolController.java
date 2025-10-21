package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import com.example.persistencia.poliglota.service.sql.RolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql/roles")
public class RolController {

    private final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @GetMapping
    public List<Rol> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Rol create(@RequestBody Rol rol) {
        return service.save(rol);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
