package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.service.mongo.ProcesoExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/procesos")
public class ProcesoExecutorController {

    private final ProcesoExecutorService executorService;

    public ProcesoExecutorController(ProcesoExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostMapping("/ejecutar")
    public ResponseEntity<String> ejecutar(
            @RequestParam Integer usuarioId,
            @RequestParam String procesoId
    ) {
        String resultado = executorService.ejecutarProceso(usuarioId, procesoId);
        return ResponseEntity.ok(resultado);
    }
}
