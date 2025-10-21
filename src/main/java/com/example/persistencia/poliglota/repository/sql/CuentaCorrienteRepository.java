package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Integer> {
    CuentaCorriente findByUsuarioIdUsuario(Integer usuarioId);
}
