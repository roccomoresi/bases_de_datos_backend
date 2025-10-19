package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.service.mongo.AlertaService;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/alertas")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertaController {

    private final AlertaRepository alertaRepository;
    private final AlertaService alertaService;

    public AlertaController(AlertaRepository alertaRepository, AlertaService alertaService) {
        this.alertaRepository = alertaRepository;
        this.alertaService = alertaService;
    }

    @GetMapping
    public List<Alerta> getAll() {
        return alertaRepository.findAll();
    }

    @PutMapping("/{id}/resolver")
    public String resolver(@PathVariable UUID id) {
        alertaService.resolver(id);
        return "✅ Alerta " + id + " marcada como resuelta.";
    }
}
