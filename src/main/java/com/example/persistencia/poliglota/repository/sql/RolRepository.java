package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Rol;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, UUID> {
}
