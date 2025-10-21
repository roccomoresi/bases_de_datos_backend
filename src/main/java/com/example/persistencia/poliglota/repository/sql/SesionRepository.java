package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionRepository extends JpaRepository<Sesion, Integer> {
}
