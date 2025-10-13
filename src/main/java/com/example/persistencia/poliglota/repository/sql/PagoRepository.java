package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Pago;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, UUID> {}
