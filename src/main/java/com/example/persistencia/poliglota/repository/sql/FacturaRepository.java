package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {}
