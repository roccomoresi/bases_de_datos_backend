package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByDescripcion(String descripcion);
}
