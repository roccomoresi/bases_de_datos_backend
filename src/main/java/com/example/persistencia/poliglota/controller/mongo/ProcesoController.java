package com.example.persistencia.poliglota.controller.mongo;

import com.example.persistencia.poliglota.dto.ProcesoMapper;
import com.example.persistencia.poliglota.dto.ProcesoResponse;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo/procesos")
public class ProcesoController {

    private final ProcesoService service;

    public ProcesoController(ProcesoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Proceso>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Proceso>> getActivos() {
        return ResponseEntity.ok(service.getActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proceso> getById(@PathVariable String id) {
        // El service lanza 404 si no existe, así que devolvemos 200 directo si llega
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Proceso>> getByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(service.getByTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<Proceso> save(@RequestBody Proceso proceso) {
        return ResponseEntity.ok(service.save(proceso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proceso> update(@PathVariable String id, @RequestBody Proceso proceso) {
        return ResponseEntity.ok(service.update(id, proceso));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Proceso> toggleEstado(@PathVariable String id) {
        return ResponseEntity.ok(service.toggleEstado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id); // lanza 404 si no existe
        return ResponseEntity.noContent().build();
    }


    //=======================================================   


    // ---------- v2 (DTO limpio) ----------
    @PostMapping("/v2")
    public ResponseEntity<ProcesoResponse> saveV2(@RequestBody Proceso proceso) {
        Proceso saved = service.save(proceso);
        return ResponseEntity.ok(ProcesoMapper.toResponse(saved));
    }

    @GetMapping("/v2")
    public ResponseEntity<List<ProcesoResponse>> getAllV2() {
        List<ProcesoResponse> out = service.getAll()
            .stream().map(ProcesoMapper::toResponse).toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/v2/{id}")
    public ResponseEntity<ProcesoResponse> getByIdV2(@PathVariable String id) {
        return ResponseEntity.ok(ProcesoMapper.toResponse(service.obtenerPorId(id)));
    }

    @GetMapping("/v2/tipo/{tipo}")
    public ResponseEntity<List<ProcesoResponse>> getByTipoV2(@PathVariable String tipo) {
        List<ProcesoResponse> out = service.getByTipo(tipo)
            .stream().map(ProcesoMapper::toResponse).toList();
        return ResponseEntity.ok(out);
    }
}
