package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Grupo;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, UUID> {
}
