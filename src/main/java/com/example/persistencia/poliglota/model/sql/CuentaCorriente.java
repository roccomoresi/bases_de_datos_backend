package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cuenta_corriente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuenta;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true) // 🔹 usa el nombre exacto de la FK en MySQL
    @JsonIgnoreProperties({"facturas", "cuentaCorriente"})
    private Usuario usuario;

    @Column(name = "saldo", nullable = false)
    private Double saldo = 0.0;
}

