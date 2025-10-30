package com.example.persistencia.poliglota.repository.sql;

import com.example.persistencia.poliglota.model.sql.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByFacturaIdFactura(Integer facturaId);

    /**
     * 🔹 Devuelve todos los pagos filtrados por método de pago (tarjeta, efectivo, etc.).
     */
    List<Pago> findByMetodoPago(Pago.MetodoPago metodoPago);

    /**
     * 🔹 Devuelve todos los pagos filtrados por estado (confirmado, pendiente, rechazado).
     */
    List<Pago> findByEstado(Pago.EstadoPago estado);
}
