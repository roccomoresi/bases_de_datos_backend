package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/procesos")
@CrossOrigin
public class ProcesoController {

    private final ProcesoService service;

    public ProcesoController(ProcesoService service) {
        this.service = service;
    }

    // ✅ Obtener todos los procesos
    @GetMapping
    public List<Proceso> getAll() {
        return service.getAll();
    }

    // ✅ Obtener proceso por ID
    @GetMapping("/{id}")
    public Optional<Proceso> getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Crear o actualizar proceso
    @PostMapping
    public Proceso save(@RequestBody Proceso proceso) {
        return service.save(proceso);
    }

    // ✅ Eliminar proceso
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
