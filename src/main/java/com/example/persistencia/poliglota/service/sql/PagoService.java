package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.persistencia.poliglota.service.mongo.SolicitudProcesoService;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;
    private final SolicitudProcesoService solicitudProcesoService;
    private final ProcesoService procesoService;


    @Transactional
public Pago registrarPago(Integer idFactura, Double montoPagado, String metodoPago) {
    Factura factura = facturaRepository.findById(idFactura)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

    // Crear el pago
    Pago pago = new Pago();
    pago.setFactura(factura);
    pago.setMontoPagado(montoPagado);
    pago.setMetodoPago(metodoPago);
    Pago savedPago = pagoRepository.save(pago);

    // Actualizar estado de factura
    factura.setEstado(Factura.EstadoFactura.PAGADA);
    facturaRepository.save(factura);

    // 🔹 Actualizar cuenta corriente
    CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());
    // 🔹 Registrar movimiento como DÉBITO
movimientoCuentaService.registrarMovimiento(
    cuenta,
    "Pago de factura #" + factura.getIdFactura(),
    montoPagado,
    MovimientoCuenta.TipoMovimiento.DEBITO
);

// 🔹 Restar el monto del saldo de la cuenta corriente
cuentaCorrienteService.actualizarSaldo(cuenta, montoPagado, false);


    // 🧠 Ejecutar proceso asociado en Mongo (si existe)
    try {
        String descripcion = factura.getDescripcionProceso(); // ej: "Solicitud de proceso: Promedio por ciudad"
        if (descripcion != null && descripcion.toLowerCase().contains("solicitud de proceso")) {

            Integer usuarioId = factura.getUsuario().getIdUsuario();

            // Buscar solicitudes pendientes del usuario
            List<com.example.persistencia.poliglota.model.mongo.SolicitudProceso> pendientes =
                    solicitudProcesoService.getByUsuario(usuarioId)
                            .stream()
                            .filter(s -> s.getEstado().equalsIgnoreCase("pendiente"))
                            .toList();

            if (!pendientes.isEmpty()) {
                var solicitud = pendientes.get(pendientes.size() - 1); // la más reciente

                // Ejecutar proceso correspondiente
                procesoService.ejecutarProceso(solicitud.getProceso().getId());

                // Marcar como completada
                solicitudProcesoService.updateEstado(solicitud.getId(), "completado");
            }
        }
    } catch (Exception e) {
        System.err.println("⚠️ No se pudo ejecutar el proceso asociado: " + e.getMessage());
    }

    return savedPago;
}


    public List<Pago> obtenerPagosPorFactura(Integer idFactura) {
        return pagoRepository.findByFactura_IdFactura(idFactura);
    }

    public List<Pago> obtenerPagosPorUsuario(Integer idUsuario) {
    return pagoRepository.findByFactura_Usuario_IdUsuario(idUsuario);
}

}
