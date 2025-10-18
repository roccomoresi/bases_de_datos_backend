package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Ciudad;
import com.example.persistencia.poliglota.service.mongo.CiudadService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/ciudades")
@CrossOrigin
public class CiudadController {

    private final CiudadService service;

    public CiudadController(CiudadService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ciudad> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Ciudad> getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public Ciudad save(@RequestBody Ciudad ciudad) {
        return service.save(ciudad);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
