package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double monto;

    private String metodoPago;

    private LocalDateTime fechaPago;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "factura_id")
    private Factura factura;


    // ✅ Constructor usado en TransaccionService
    public Pago(Double monto, String metodoPago, LocalDateTime fechaPago, Factura factura) {
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.factura = factura;
    }
}
