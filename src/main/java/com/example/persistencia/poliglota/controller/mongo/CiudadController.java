package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.model.mongo.Ciudad;
import com.example.persistencia.poliglota.service.mongo.CiudadMongoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/ciudades")
public class CiudadController {

    private final CiudadService service;

    public CiudadController(CiudadMongoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ciudad> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Ciudad save(@RequestBody Ciudad ciudad) {
        return service.save(ciudad);
    }

    @GetMapping("/pais/{pais}")
    public List<Ciudad> findByPais(@PathVariable String pais) {
        return service.findByPais(pais);
    }
}
