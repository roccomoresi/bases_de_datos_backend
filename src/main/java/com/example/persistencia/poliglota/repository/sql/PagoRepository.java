package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByFacturaIdFactura(Integer facturaId);
}
