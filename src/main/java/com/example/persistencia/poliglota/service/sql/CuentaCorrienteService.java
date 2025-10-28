package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.repository.sql.CuentaCorrienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaCorrienteService {

    private final CuentaCorrienteRepository repository;

    public CuentaCorrienteService(CuentaCorrienteRepository repository) {
        this.repository = repository;
    }

    public List<CuentaCorriente> getAll() {
        return repository.findAll();
    }

    public CuentaCorriente getByUsuario(Integer usuarioId) {
        return repository.findByUsuarioIdUsuario(usuarioId);
    }

    public CuentaCorriente save(CuentaCorriente cuenta) {
        return repository.save(cuenta);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByUsuarioId(Integer usuarioId) {
        repository.deleteByUsuario_IdUsuario(usuarioId);
    }

    /* ───────────────────────────────────────────────
       📈 Ajustar saldo e historial
    ─────────────────────────────────────────────── */
    public void ajustarSaldo(Integer usuarioId, Double monto, String motivo) {
        CuentaCorriente cuenta = repository.findByUsuarioIdUsuario(usuarioId);
        if (cuenta == null) return;

        double nuevoSaldo = cuenta.getSaldoActual() + monto;
        cuenta.setSaldoActual(nuevoSaldo);
        cuenta.agregarMovimiento(motivo + " (" + monto + ")");
        repository.save(cuenta);
    }
}
