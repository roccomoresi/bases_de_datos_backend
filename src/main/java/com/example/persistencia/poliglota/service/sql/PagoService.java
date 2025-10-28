package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.Pago;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.repository.sql.PagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaService facturaService;
    private final CuentaCorrienteService cuentaService;

    public PagoService(PagoRepository pagoRepository, FacturaService facturaService, CuentaCorrienteService cuentaService) {
        this.pagoRepository = pagoRepository;
        this.facturaService = facturaService;
        this.cuentaService = cuentaService;
    }

    public List<Pago> getAll() {
        return pagoRepository.findAll();
    }

    public List<Pago> getByFactura(Integer facturaId) {
        return pagoRepository.findByFacturaIdFactura(facturaId);
    }

    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void delete(Integer id) {
        pagoRepository.deleteById(id);
    }

    /* ───────────────────────────────────────────────
       💰 Registrar un nuevo pago y actualizar saldo
    ─────────────────────────────────────────────── */
    public Pago registrarPago(Factura factura, Double monto, Pago.MetodoPago metodo) {
        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMonto(monto);
        pago.setMetodoPago(metodo);
        pago.setFechaPago(LocalDateTime.now());

        // 🔹 Guardar pago
        Pago pagoGuardado = pagoRepository.save(pago);

        // 🔹 Marcar factura como pagada
        facturaService.marcarComoPagada(factura);

        // 🔹 Actualizar cuenta corriente del usuario
        cuentaService.ajustarSaldo(factura.getUsuario().getIdUsuario(), -monto, 
                "Pago de factura #" + factura.getIdFactura());

        return pagoGuardado;
    }
}
