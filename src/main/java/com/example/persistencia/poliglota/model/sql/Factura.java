package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEmision;

    private Double monto;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // ✅ Constructor personalizado (usado en TransaccionService)
    public Factura(LocalDate fecha, Double monto, Usuario usuario) {
        this.fechaEmision = fecha;
        this.monto = monto;
        this.usuario = usuario;
        this.estado = "PENDIENTE";
    }
}
