package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "descripcion_proceso")
    private String descripcionProceso; // 🔹 referencia lógica al proceso en Mongo

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Pago> pagos;

    public enum EstadoFactura {
        PENDIENTE,
        PAGADA,
        VENCIDA
    }
}
