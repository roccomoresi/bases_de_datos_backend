package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.CuentaCorrienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaCorrienteService {

    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final MovimientoCuentaService movimientoCuentaService;


    @Transactional
    public CuentaCorriente crearSiNoExiste(Usuario usuario) {
        return cuentaCorrienteRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    CuentaCorriente c = new CuentaCorriente();
                    c.setUsuario(usuario);
                    c.setSaldo(100000.0);

                    // Primero persistimos la cuenta para evitar referencias transientes
                    CuentaCorriente saved = cuentaCorrienteRepository.save(c);

                    // Registrar el saldo inicial como CREDITO
                    movimientoCuentaService.registrarMovimiento(
                            saved,
                            "Saldo inicial asignado para pruebas",
                            100000.0,
                            MovimientoCuenta.TipoMovimiento.CREDITO
                    );

                    return saved;
                });
    }

    @Transactional
    public void actualizarSaldo(CuentaCorriente cuenta, Double monto, boolean esCredito) {
        Double nuevoSaldo = cuenta.getSaldo() + (esCredito ? monto : -monto);
        cuenta.setSaldo(nuevoSaldo);
        cuentaCorrienteRepository.save(cuenta);
    }

    public CuentaCorriente obtenerPorUsuario(Usuario usuario) {
        return cuentaCorrienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Cuenta corriente no encontrada para el usuario"));
    }

    public List<CuentaCorriente> obtenerTodas() {
    return cuentaCorrienteRepository.findAll();
}

public CuentaCorriente obtenerPorId(Integer idCuenta) {
    return cuentaCorrienteRepository.findById(idCuenta)
            .orElseThrow(() -> new RuntimeException("Cuenta corriente no encontrada"));
}

}
