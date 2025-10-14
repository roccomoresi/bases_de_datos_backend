package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate fechaEmision;

    private Double monto;

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ✅ Constructor personalizado (usado en TransaccionService)
    public Factura(LocalDate fecha, Double monto, Usuario usuario) {
        this.fechaEmision = fecha;
        this.monto = monto;
        this.usuario = usuario;
        this.estado = "PENDIENTE";
    }
}
