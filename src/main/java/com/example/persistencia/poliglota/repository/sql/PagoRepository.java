package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByFactura_IdFactura(Integer idFactura);
List<Pago> findByFactura_Usuario_IdUsuario(Integer idUsuario);

}
