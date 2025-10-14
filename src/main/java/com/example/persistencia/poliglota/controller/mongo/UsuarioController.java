package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.UsuarioMongo;
import com.example.persistencia.poliglota.service.mongo.UsuarioMongoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/usuarios")
public class UsuarioMongoController {

    private final UsuarioMongoService service;

    public UsuarioMongoController(UsuarioMongoService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioMongo> getAll() {
        return service.getAll();
    }

    @PostMapping
    public UsuarioMongo save(@RequestBody UsuarioMongo usuario) {
        return service.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteById(id);
    }
}
