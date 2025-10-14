package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    // Buscar todas las facturas de un usuario
    List<Factura> findByUsuario(Usuario usuario);

    // Buscar una factura específica por su ID (ya existe por defecto en JpaRepository)
    Optional<Factura> findById(UUID id);
}
