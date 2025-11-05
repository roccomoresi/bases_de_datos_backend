package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;

    /* ───────────────────────────────
       🧾 CREAR FACTURA COMPLETA (impacta cuenta corriente)
    ─────────────────────────────── */
    @Transactional
    public Factura crearFactura(Factura factura) {
        // Guardar factura
        Factura saved = facturaRepository.save(factura);

        // Obtener o crear la cuenta corriente del usuario
        CuentaCorriente cuenta = cuentaCorrienteService.crearSiNoExiste(factura.getUsuario());

        // 🔹 Registrar movimiento tipo DEBITO (Factura = resta saldo)
        movimientoCuentaService.registrarMovimiento(
                cuenta,
                "Factura #" + saved.getIdFactura() + " generada",
                saved.getTotal(),
                MovimientoCuenta.TipoMovimiento.DEBITO
        );

        // 🔹 Actualizar saldo (restar total)
        cuentaCorrienteService.actualizarSaldo(cuenta, saved.getTotal(), false);

        return saved;
    }

    /* ───────────────────────────────
       📋 OBTENER FACTURAS POR USUARIO
    ─────────────────────────────── */
    public List<Factura> obtenerFacturasPorUsuario(Integer idUsuario) {
        return facturaRepository.findByUsuario_IdUsuarioOrderByFechaEmisionDesc(idUsuario);
    }

    /* ───────────────────────────────
       💰 MARCAR COMO PAGADA
    ─────────────────────────────── */
    @Transactional
    public Factura marcarComoPagada(Integer idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        factura.setEstado(Factura.EstadoFactura.PAGADA);
        return facturaRepository.save(factura);
    }

    /* ───────────────────────────────
       📜 OBTENER TODAS LAS FACTURAS
    ─────────────────────────────── */
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    /* ───────────────────────────────
       🧾 GENERAR FACTURA (con impacto contable)
    ─────────────────────────────── */
    @Transactional
    public Factura generarFactura(Integer usuarioId, String descripcion, Double monto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setDescripcionProceso(descripcion);
        factura.setTotal(monto);
        factura.setEstado(Factura.EstadoFactura.EMITIDA);
        factura.setFechaEmision(LocalDateTime.now());

        return crearFactura(factura);
    }

    /* ───────────────────────────────
       🕓 GENERAR FACTURA PENDIENTE (sin impacto contable)
    ─────────────────────────────── */
    @Transactional
    public void generarFacturaPendiente(Integer usuarioId, String descripcion, Double monto, String procesoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setDescripcionProceso(descripcion);
        factura.setTotal(monto);
        factura.setEstado(Factura.EstadoFactura.PENDIENTE);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setProcesoId(procesoId); // 🔗 vincula factura con proceso técnico (Mongo)

        facturaRepository.save(factura);
    }
}
