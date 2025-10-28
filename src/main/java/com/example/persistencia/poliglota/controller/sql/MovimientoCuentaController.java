package com.example.persistencia.poliglota.controller.sql;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.service.sql.MovimientoCuentaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sql/movimientos")
public class MovimientoCuentaController {

    private final MovimientoCuentaService service;

    public MovimientoCuentaController(MovimientoCuentaService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovimientoCuenta> getAll() {
        return service.getAll();
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<MovimientoCuenta> getByCuenta(@PathVariable Integer cuentaId) {
        return service.getByCuenta(cuentaId);
    }

    @PostMapping
    public MovimientoCuenta create(@RequestBody MovimientoCuenta movimiento) {
        return service.save(movimiento);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
