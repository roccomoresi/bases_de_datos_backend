package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // ✅ el correcto, de java.util

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Spring Data JPA genera automáticamente la query por el nombre del método
    Optional<Usuario> findByEmail(String email);
}
