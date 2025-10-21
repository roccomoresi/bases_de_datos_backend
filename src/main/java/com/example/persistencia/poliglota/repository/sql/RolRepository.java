package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}
