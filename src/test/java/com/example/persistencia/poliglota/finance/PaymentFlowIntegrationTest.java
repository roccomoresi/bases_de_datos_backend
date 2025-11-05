package com.example.persistencia.poliglota.finance;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.sql.CuentaCorriente;
import com.example.persistencia.poliglota.model.sql.Factura;
import com.example.persistencia.poliglota.model.sql.MovimientoCuenta;
import com.example.persistencia.poliglota.model.sql.Usuario;
import com.example.persistencia.poliglota.repository.sql.CuentaCorrienteRepository;
import com.example.persistencia.poliglota.repository.sql.FacturaRepository;
import com.example.persistencia.poliglota.repository.sql.MovimientoCuentaRepository;
import com.example.persistencia.poliglota.repository.sql.UsuarioRepository;
import com.example.persistencia.poliglota.repository.sql.SesionRepository;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;
import com.example.persistencia.poliglota.service.mongo.SolicitudProcesoService;
import com.example.persistencia.poliglota.service.sql.FacturaService;
import com.example.persistencia.poliglota.service.sql.PagoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FacturaService.class, PagoService.class, com.example.persistencia.poliglota.service.sql.CuentaCorrienteService.class, com.example.persistencia.poliglota.service.sql.MovimientoCuentaService.class})
@ActiveProfiles("test")
class PaymentFlowIntegrationTest {

    @MockBean
    SolicitudProcesoService solicitudProcesoService; // Evita acceso real a Mongo

    @MockBean
    ProcesoService procesoService; // Evita acceso real a Mongo

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FacturaRepository facturaRepository;
    @Autowired private CuentaCorrienteRepository cuentaCorrienteRepository;
    @Autowired private MovimientoCuentaRepository movimientoCuentaRepository;
    @Autowired private SesionRepository sesionRepository;
    @Autowired private FacturaService facturaService;
    @Autowired private PagoService pagoService;
    @Autowired private TestEventListener testEventListener;
    @Autowired private com.example.persistencia.poliglota.repository.sql.PagoRepository pagoRepository;

    @AfterEach
    void cleanup() {
        // Orden seguro y en batch para evitar flush de entidades transientes
        pagoRepository.deleteAllInBatch();
        movimientoCuentaRepository.deleteAllInBatch();
        sesionRepository.deleteAllInBatch();
        facturaRepository.deleteAllInBatch();
        cuentaCorrienteRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        testEventListener.reset();
    }

    @Test
    void pagarFactura_persisteMovimientoCredito_actualizaSaldo_emiteEvento() throws Exception {
        // 1) Crear usuario
Usuario u = new Usuario();
u.setNombreCompleto("Usuario Test");
u.setEmail("user" + System.nanoTime() + "@test.local");
u.setContrasena("secret");
u = usuarioRepository.save(u);

// 2) Generar factura PENDIENTE sin impacto contable
double monto = 500.0;
facturaService.generarFacturaPendiente(u.getIdUsuario(), "Test factura", monto, "TEST-PROCESO");

// Recuperar última factura del usuario
List<Factura> facturas = facturaRepository.findByUsuario_IdUsuarioOrderByFechaEmisionDesc(u.getIdUsuario());
Assertions.assertFalse(facturas.isEmpty(), "Debe existir al menos una factura pendiente");
Factura factura = facturas.get(0);
Integer facturaId = factura.getIdFactura();


        // 3) Registrar pago: debe crear CC si no existe, sumar saldo y registrar CREDITO
        pagoService.registrarPago(facturaId, monto, "TEST");

        // 4) Assert factura PAGADA
        Factura facturaUpdated = facturaRepository.findById(facturaId).orElseThrow();
        Assertions.assertEquals(Factura.EstadoFactura.PAGADA, facturaUpdated.getEstado(), "La factura debe estar PAGADA");

        // 5) Assert cuenta corriente con saldo inicial + pago
        CuentaCorriente cuenta = cuentaCorrienteRepository.findByUsuario(u).orElseThrow();
        Assertions.assertEquals(100000.0 + monto, cuenta.getSaldo(), 0.001, "Saldo debe reflejar el CREDITO del pago");

        // 6) Assert movimiento CREDITO del pago existe
        List<MovimientoCuenta> movs = movimientoCuentaRepository.findByCuentaCorriente_IdCuentaOrderByFechaDesc(cuenta.getIdCuenta());
        Optional<MovimientoCuenta> movPago = movs.stream()
                .filter(m -> m.getDescripcion() != null && m.getDescripcion().contains("Pago de factura #" + facturaId))
                .findFirst();
        Assertions.assertTrue(movPago.isPresent(), "Debe existir movimiento de pago");
        Assertions.assertEquals(MovimientoCuenta.TipoMovimiento.CREDITO, movPago.get().getTipoMovimiento());
        Assertions.assertEquals(monto, movPago.get().getMonto(), 0.001);

        // 7) Assert se emitió el evento y fue capturado por el listener de prueba
        boolean received = testEventListener.await(2, TimeUnit.SECONDS);
        Assertions.assertTrue(received, "Debe emitirse FacturaPagadaEvent");
        Assertions.assertNotNull(testEventListener.getLastEvent());
        Assertions.assertEquals(facturaId, testEventListener.getLastEvent().getFacturaId());
        Assertions.assertEquals(u.getIdUsuario(), testEventListener.getLastEvent().getUsuarioId());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        TestEventListener testEventListener() {
            return new TestEventListener();
        }
    }

    static class TestEventListener {
        private volatile FacturaPagadaEvent lastEvent;
        private final CountDownLatch latch = new CountDownLatch(1);

        @EventListener
        public void onFacturaPagada(FacturaPagadaEvent event) {
            this.lastEvent = event;
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        FacturaPagadaEvent getLastEvent() {
            return lastEvent;
        }

        void reset() {
            while (latch.getCount() > 0) {
                // no-op; CountDownLatch cannot be reset, so we ignore here
                break;
            }
            lastEvent = null;
        }
    }
}
