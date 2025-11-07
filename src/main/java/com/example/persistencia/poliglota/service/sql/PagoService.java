package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /**
     * 💰 Registra un pago, actualiza contabilidad y dispara el evento técnico.
     */
    @Transactional
    public Pago registrarPago(Integer idFactura, Double montoPagado, String metodoPago) {
        // 1️⃣ Buscar factura
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + idFactura));

        // 2️⃣ Registrar el pago
        Pago pago = Pago.builder()
                .factura(factura)
                .montoPagado(montoPagado)
                .metodoPago(metodoPago)
                .fechaPago(LocalDateTime.now())
                .build();

        Pago savedPago = pagoRepository.save(pago);

        // 3️⃣ Marcar factura como pagada
        factura = facturaService.marcarComoPagada(idFactura);

        // 4️⃣ Impacto contable (resta saldo al usuario)
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                "Pago de factura #" + factura.getIdFactura(),
                montoPagado,
                MovimientoCuenta.TipoMovimiento.DEBITO
        );
        cuentaCorrienteService.actualizarSaldo(cuenta, montoPagado, false);

        // 5️⃣ Publicar evento (SQL → Mongo/Cassandra)
        try {
            eventPublisher.publishEvent(new FacturaPagadaEvent(
                    factura.getIdFactura(),
                    factura.getUsuario() != null ? factura.getUsuario().getIdUsuario() : null,
                    factura.getDescripcionProceso(),
                    factura.getProcesoId() // 🔗 nuevo: vincula la factura con el proceso Mongo
            ));

            System.out.printf(
                    "📨 Evento FacturaPagadaEvent emitido: factura=%d, usuario=%d, proceso=%s%n",
                    factura.getIdFactura(),
                    factura.getUsuario() != null ? factura.getUsuario().getIdUsuario() : null,
                    factura.getProcesoId()
            );
        } catch (Exception e) {
            System.err.println("⚠️ Error al emitir FacturaPagadaEvent: " + e.getMessage());
        }

        return savedPago;
    }

    // 🔹 Listar todos los pagos
    public List<Pago> getAll() {
        return pagoRepository.findAll();
    }

    // 🔹 Obtener pagos por factura
    public List<Pago> obtenerPagosPorFactura(Integer idFactura) {
        return pagoRepository.findByFactura_IdFactura(idFactura);
    }

    // 🔹 Obtener pagos por usuario
    public List<Pago> obtenerPagosPorUsuario(Integer idUsuario) {
        return pagoRepository.findByFactura_Usuario_IdUsuario(idUsuario);
    }

    // 🔹 Alias de getAll (por compatibilidad con otros servicios)
    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }
}
