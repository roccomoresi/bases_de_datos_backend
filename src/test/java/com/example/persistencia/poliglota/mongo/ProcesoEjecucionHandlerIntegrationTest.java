package com.example.persistencia.poliglota.mongo;

import com.example.persistencia.poliglota.events.FacturaPagadaEvent;
import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.service.mongo.ProcesoService;
import com.example.persistencia.poliglota.service.mongo.SolicitudProcesoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProcesoEjecucionHandlerIntegrationTest {

    @MockBean
    SolicitudProcesoService solicitudProcesoService; // mock para no usar Mongo real

    @MockBean
    ProcesoService procesoService; // mock de ejecución de proceso

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    @Transactional
    void listener_cambiaEstadosYEjecutaProceso_alPagarFactura() {
        // Arrange: solicitud pendiente con proceso asociado
        Integer usuarioId = 999;
        Proceso proceso = new Proceso();
        proceso.setNombre("Proceso Demo");
        proceso.setTipo("servicio");
        proceso.setCosto(new BigDecimal("123.45"));

        SolicitudProceso solicitud = new SolicitudProceso(usuarioId, proceso);
        solicitud.setEstado("pendiente");

        when(solicitudProcesoService.getByUsuario(usuarioId)).thenReturn(List.of(solicitud));

        // Act: publicar evento dentro de TX y comprometer
        publisher.publishEvent(new FacturaPagadaEvent(1, usuarioId, "Solicitud del proceso: Demo"));
        TestTransaction.flagForCommit();
        TestTransaction.end(); // dispara AFTER_COMMIT

        // Assert: se cambió a en_progreso → completado y se ejecutó el proceso
        verify(solicitudProcesoService, timeout(1000)).updateEstado(any(), eq("en_progreso"));
        verify(procesoService, timeout(1000)).ejecutarProceso(eq(proceso.getId()));
        verify(solicitudProcesoService, timeout(1000)).updateEstado(any(), eq("completado"));

        // Verifica que no haya más interacciones inesperadas
        verifyNoMoreInteractions(procesoService);
    }
}
