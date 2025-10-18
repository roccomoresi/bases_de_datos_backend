package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Mensaje;
import com.example.persistencia.poliglota.service.mongo.MensajeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mongo/mensajes")
public class MensajeController {

    private final MensajeService service;

    public MensajeController(MensajeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mensaje> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Mensaje getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public Mensaje save(@RequestBody Mensaje mensaje) {
        return service.save(mensaje);
    }

    @PutMapping("/{id}")
    public Mensaje update(@PathVariable UUID id, @RequestBody Mensaje mensaje) {
        return service.update(id, mensaje);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
