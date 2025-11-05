package com.example.persistencia.poliglota.model.sql;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonBackReference // evita recursión infinita hacia Factura
    private Factura factura;

    @Column(name = "monto_pagado", nullable = false)
    private Double montoPagado;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago; // TRANSFERENCIA, TARJETA, EFECTIVO...

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
}
