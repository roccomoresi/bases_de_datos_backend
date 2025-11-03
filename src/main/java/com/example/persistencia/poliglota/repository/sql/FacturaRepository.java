package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByUsuario_IdUsuarioOrderByFechaEmisionDesc(Integer idUsuario);
}
