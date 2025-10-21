package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuenta, Integer> {
    List<MovimientoCuenta> findByCuentaIdCuenta(Integer cuentaId);
}
