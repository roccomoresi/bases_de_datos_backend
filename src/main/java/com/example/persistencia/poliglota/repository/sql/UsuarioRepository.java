package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.model.sql.Usuario.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Filtrar por estado
    List<Usuario> findByEstado(EstadoUsuario estado);

    // Buscar por rol
    List<Usuario> findByRol_Descripcion(String descripcion);
}
