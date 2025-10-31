package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.persistencia.poliglota.model.sql.Usuario;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFactura;

    private Double montoTotal = 0.0;

    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado = EstadoFactura.pendiente;

    public enum EstadoFactura {
        pendiente, pagada, vencida
    }

    // 🔹 Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // 🔹 Relación con Pagos
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    // 🔹 Relación con procesos facturados (vinculación lógica con Mongo)
    private String procesosFacturados; 
    // Podés guardar IDs de procesos o nombres concatenados, por ejemplo: "informe-01, alerta-02"

    // Getters y Setters
    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public EstadoFactura getEstado() { return estado; }
    public void setEstado(EstadoFactura estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }

    public String getProcesosFacturados() { return procesosFacturados; }
    public void setProcesosFacturados(String procesosFacturados) { this.procesosFacturados = procesosFacturados; }
}
