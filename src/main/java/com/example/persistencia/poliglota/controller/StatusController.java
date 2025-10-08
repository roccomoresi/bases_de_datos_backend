package com.example.persistencia.poliglota.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


//ENDPOINT DE PRUEBA PARA LEVANTAR TODO
@RestController
public class StatusController {

    @GetMapping("/status")
    public Map<String, String> status() {
        Map<String, String> estado = new HashMap<>();
        estado.put("status", "✅ Aplicación corriendo correctamente");
        estado.put("mysql", "OK");
        estado.put("cassandra", "OK");
        estado.put("neo4j", "OK");
        return estado;
    }
}
