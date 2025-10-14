package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.service.mongo.ProcesoMongoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/procesos")
public class ProcesoController {

    private final ProcesoMongoService service;

    public ProcesoMongoController(ProcesoMongoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Proceso> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Proceso save(@RequestBody Proceso proceso) {
        return service.save(proceso);
    }
}
