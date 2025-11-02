package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;

    @Transactional
    public Pago registrarPago(Integer idFactura, Double montoPagado, String metodoPago) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Crear el pago
        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMontoPagado(montoPagado);
        pago.setMetodoPago(metodoPago);

        // Guardar el pago
        Pago savedPago = pagoRepository.save(pago);

        // Actualizar estado de factura
        factura.setEstado(Factura.EstadoFactura.PAGADA);
        facturaRepository.save(factura);

        // 🔹 Obtener o crear la cuenta corriente del usuario
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());

        // 🔹 Registrar movimiento tipo CREDITO (Pago = suma saldo)
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                "Pago de factura #" + factura.getIdFactura(),
                montoPagado,
                MovimientoCuenta.TipoMovimiento.CREDITO
        );

        // 🔹 Actualizar saldo (sumar total)
        cuentaCorrienteService.actualizarSaldo(cuenta, montoPagado, true);

        return savedPago;
    }

    public List<Pago> obtenerPagosPorFactura(Integer idFactura) {
        return pagoRepository.findByFactura_IdFactura(idFactura);
    }

    public List<Pago> obtenerPagosPorUsuario(Integer idUsuario) {
    return pagoRepository.findByFactura_Usuario_IdUsuario(idUsuario);
}

}
