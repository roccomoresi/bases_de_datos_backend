package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.repository.sql.MovimientoCuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MovimientoCuentaService {
    private final MovimientoCuentaRepository repository;

    public MovimientoCuentaService(MovimientoCuentaRepository repository) {
        this.repository = repository;
    }

    public List<MovimientoCuenta> getAll() {
        return repository.findAll();
    }

    public List<MovimientoCuenta> getByCuenta(Integer cuentaId) {
        return repository.findByCuentaIdCuenta(cuentaId);
    }

    public MovimientoCuenta save(MovimientoCuenta movimiento) {
        return repository.save(movimiento);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
