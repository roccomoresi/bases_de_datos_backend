package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuenta, Integer> {
    List<MovimientoCuenta> findByCuentaCorriente_IdCuentaOrderByFechaDesc(Integer idCuenta);
}
