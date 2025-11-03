package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;

    // 🔹 Registrar pago (ya estaba correcto)
    @Transactional
    public Pago registrarPago(Integer idFactura, Double montoPagado, String metodoPago) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMontoPagado(montoPagado);
        pago.setMetodoPago(metodoPago);

        Pago savedPago = pagoRepository.save(pago);

        factura.setEstado(Factura.EstadoFactura.PAGADA);
        facturaRepository.save(factura);

        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                "Pago de factura #" + factura.getIdFactura(),
                montoPagado,
                MovimientoCuenta.TipoMovimiento.CREDITO
        );
        cuentaCorrienteService.actualizarSaldo(cuenta, montoPagado, true);

        return savedPago;
    }

    // 🔹 Listar todos los pagos (para GET /pagos)
    public List<Pago> getAll() {
        return pagoRepository.findAll();
    }

    // 🔹 Obtener pagos por factura (para GET /pagos/factura/{id})
    public List<Pago> obtenerPagosPorFactura(Integer idFactura) {
        return pagoRepository.findByFactura_IdFactura(idFactura);
    }

    // 🔹 Obtener pagos por usuario (ya lo tenías)
    public List<Pago> obtenerPagosPorUsuario(Integer idUsuario) {
        return pagoRepository.findByFactura_Usuario_IdUsuario(idUsuario);
    }

    // 🔹 Eliminar pago (para DELETE /pagos/{id})
    public void delete(Integer idPago) {
        pagoRepository.deleteById(idPago);
    }
}
