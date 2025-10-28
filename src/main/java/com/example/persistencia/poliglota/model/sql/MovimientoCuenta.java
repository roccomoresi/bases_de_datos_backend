package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Data
@Table(name = "movimiento_cuenta")
public class MovimientoCuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimiento;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private CuentaCorriente cuenta;

    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private Double monto;
    private String descripcion;

    public enum TipoMovimiento {
        DEBITO, CREDITO
    }
}
