package com.example.persistencia.poliglota.dto;

import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorrienteResponse {
    private Integer idCuenta;
    private Double saldoActual;
    private List<MovimientoCuenta> movimientos;
}
