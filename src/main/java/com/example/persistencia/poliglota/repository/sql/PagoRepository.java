package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {}
