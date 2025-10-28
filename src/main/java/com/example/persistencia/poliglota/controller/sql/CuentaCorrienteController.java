package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.service.sql.CuentaCorrienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/cuentas")
public class CuentaCorrienteController {

    private final CuentaCorrienteService service;

    public CuentaCorrienteController(CuentaCorrienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<CuentaCorriente> getAll() {
        return service.getAll();
    }

    @GetMapping("/usuario/{usuarioId}")
    public CuentaCorriente getByUsuario(@PathVariable Integer usuarioId) {
        return service.getByUsuario(usuarioId);
    }

    @PostMapping
    public CuentaCorriente create(@RequestBody CuentaCorriente cuenta) {
        return service.save(cuenta);
    }

    @PutMapping("/{id}")
    public CuentaCorriente update(@PathVariable Integer id, @RequestBody CuentaCorriente cuenta) {
        cuenta.setIdCuenta(id);
        return service.save(cuenta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
