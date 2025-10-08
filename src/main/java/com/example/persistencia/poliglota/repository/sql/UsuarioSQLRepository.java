package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.UsuarioSQL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioSQLRepository extends JpaRepository<UsuarioSQL, Long> {}
