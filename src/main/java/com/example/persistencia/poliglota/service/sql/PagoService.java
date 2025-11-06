package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final FacturaService facturaService;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;
    private final ApplicationEventPublisher eventPublisher;

    // 🔹 Registrar pago (ahora con DÉBITO en lugar de CREDITO)
    @Transactional
    public Pago registrarPago(Integer idFactura, Double montoPagado, String metodoPago) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMontoPagado(montoPagado);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        Pago savedPago = pagoRepository.save(pago);

        // Marcar factura como PAGADA (vía servicio)
        factura = facturaService.marcarComoPagada(idFactura);

        // 🔹 Impacto contable del pago: DÉBITO (resta saldo)
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                "Pago de factura #" + factura.getIdFactura(),
                montoPagado,
                MovimientoCuenta.TipoMovimiento.DEBITO // ← cambio clave
        );
        cuentaCorrienteService.actualizarSaldo(cuenta, montoPagado, false); // ← cambio clave

        // 🔔 Publicar evento para ejecución técnica asíncrona
        eventPublisher.publishEvent(new FacturaPagadaEvent(
                factura.getIdFactura(),
                factura.getUsuario() != null ? factura.getUsuario().getIdUsuario() : null,
                factura.getDescripcionProceso()
        ));

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

    // 🔹 Obtener pagos por usuario
    public List<Pago> obtenerPagosPorUsuario(Integer idUsuario) {
        return pagoRepository.findByFactura_Usuario_IdUsuario(idUsuario);
    }

    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }
}
