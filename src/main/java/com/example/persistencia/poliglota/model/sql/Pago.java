package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    private LocalDateTime fechaPago = LocalDateTime.now();
    private Double montoPagado;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago = MetodoPago.otros;

    public enum MetodoPago {
        tarjeta, transferencia, efectivo, otros
    }
}
