package com.example.persistencia.poliglota.controller.neo4j;

import com.example.persistencia.poliglota.model.neo4j.Alerta;
import com.example.persistencia.poliglota.service.neo4j.AlertaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public List<Alerta> getAll() {
        return alertaService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Alerta> getById(@PathVariable UUID id) {
        return alertaService.getById(id);
    }

    @PostMapping
    public Alerta create(@RequestBody Alerta alerta) {
        return alertaService.save(alerta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        alertaService.delete(id);
    }
}
