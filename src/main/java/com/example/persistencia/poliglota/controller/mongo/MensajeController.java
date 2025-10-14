package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Mensaje;
import com.example.persistencia.poliglota.service.mongo.MensajeMongoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/mensajes")
public class MensajeMongoController {

    private final MensajeMongoService service;

    public MensajeMongoController(MensajeMongoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mensaje> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Mensaje save(@RequestBody Mensaje mensaje) {
        return service.save(mensaje);
    }

    @GetMapping("/usuario/{email}")
    public List<Mensaje> getByUsuario(@PathVariable String email) {
        return service.findByUsuarioEmail(email);
    }
}
