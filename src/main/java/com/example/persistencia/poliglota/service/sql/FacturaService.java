package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.*;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final MovimientoCuentaService movimientoCuentaService;

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

    public List<Factura> obtenerFacturasPorUsuario(Integer idUsuario) {
        return facturaRepository.findByUsuario_IdUsuarioOrderByFechaEmisionDesc(idUsuario);
    }

    @Transactional
    public Factura marcarComoPagada(Integer idFactura) {
        Factura f = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        f.setEstado(Factura.EstadoFactura.PAGADA);
        return facturaRepository.save(f);
    }

    /**
     * Genera una factura asociada a un proceso y la vincula al usuario.
     */
    @Transactional
    public Factura generarFactura(Integer idUsuario, String descripcion, double monto) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura f = new Factura();
        f.setUsuario(u);
        f.setTotal(monto);
        f.setDescripcionProceso(descripcion);

        return crearFactura(f);
    }
    public Optional<Factura> getById(Integer id) {
        return facturaRepository.findById(id);
    }
    
}
