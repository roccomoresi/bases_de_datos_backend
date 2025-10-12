package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioSQLRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}

