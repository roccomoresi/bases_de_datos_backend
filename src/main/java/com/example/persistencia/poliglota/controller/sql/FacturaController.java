package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sql/facturas")
public class FacturaController {

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Factura> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Factura> getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Factura> getByUsuario(@PathVariable Integer usuarioId) {
        return service.getByUsuario(usuarioId);
    }

    @PostMapping
    public Factura create(@RequestBody Factura factura) {
        return service.save(factura);
    }

    @PutMapping("/{id}")
    public Factura update(@PathVariable Integer id, @RequestBody Factura factura) {
        factura.setIdFactura(id);
        return service.save(factura);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
