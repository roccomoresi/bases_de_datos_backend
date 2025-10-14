package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, UUID> {
    List<Factura> findByUsuario(Usuario usuario);
}
