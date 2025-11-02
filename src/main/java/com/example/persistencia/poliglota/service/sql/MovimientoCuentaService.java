package com.example.persistencia.poliglota.service.sql;

import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.repository.sql.MovimientoCuentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoCuentaService {

    private final MovimientoCuentaRepository movimientoCuentaRepository;

    /**
     * 🔹 Registrar un movimiento asociado a una cuenta (cargo o abono)
     */
    @Transactional
    public MovimientoCuenta registrarMovimiento(CuentaCorriente cuenta, String descripcion, Double monto, MovimientoCuenta.TipoMovimiento tipo) {
        MovimientoCuenta mov = new MovimientoCuenta();
        mov.setCuentaCorriente(cuenta);
        mov.setDescripcion(descripcion);
        mov.setMonto(monto);
        mov.setTipoMovimiento(tipo);
        return movimientoCuentaRepository.save(mov);
    }

    /**
     * 🔹 Obtener todos los movimientos de una cuenta corriente específica
     */
    public List<MovimientoCuenta> getByCuenta(Integer idCuenta) {
        return movimientoCuentaRepository.findByCuentaCorriente_IdCuentaOrderByFechaDesc(idCuenta);
    }

    /**
     * 🔹 (Alias) Versión con el nombre anterior para compatibilidad
     */
    public List<MovimientoCuenta> obtenerPorCuenta(Integer idCuenta) {
        return getByCuenta(idCuenta);
    }

    /**
     * 🔹 Obtener todos los movimientos del sistema
     */
    public List<MovimientoCuenta> getAll() {
        return movimientoCuentaRepository.findAll();
    }

    /**
     * 🔹 Guardar un movimiento (manual/test)
     */
    @Transactional
    public MovimientoCuenta save(MovimientoCuenta movimiento) {
        return movimientoCuentaRepository.save(movimiento);
    }

    /**
     * 🔹 Eliminar un movimiento (solo para pruebas)
     */
    @Transactional
    public void delete(Integer idMovimiento) {
        movimientoCuentaRepository.deleteById(idMovimiento);
    }
}
