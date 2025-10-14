package com.example.persistencia.poliglota.controller.neo4j;

import com.example.persistencia.poliglota.model.mongo.SensorNode;
import com.example.persistencia.poliglota.service.neo4j.SensorNodeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sensores-relaciones")
@CrossOrigin
public class SensorNodeController {

    private final SensorNodeService sensorNodeService;

    public SensorNodeController(SensorNodeService sensorNodeService) {
        this.sensorNodeService = sensorNodeService;
    }

    // @GetMapping
    // public List<SensorNode> getAll() {
    //     return sensorNodeService.getAll();
    // }

    // @PostMapping
    // public SensorNode save(@RequestBody SensorNode sensorNode) {
    //     return sensorNodeService.save(sensorNode);
    // }
}
