package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Data
@Table(name = "cuenta_corriente")
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCuenta;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    private Double saldoActual = 0.0;

    // ✅ Métodos explícitos
    public Integer getIdCuenta() { 
        return idCuenta; 
    }

    public void setIdCuenta(Integer idCuenta) { 
        this.idCuenta = idCuenta; 
    }

    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }

    public void setSaldoActual(Double saldoActual) { 
        this.saldoActual = saldoActual; 
    }
}
