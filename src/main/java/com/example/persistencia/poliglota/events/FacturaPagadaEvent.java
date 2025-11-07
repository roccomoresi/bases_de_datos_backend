package com.example.persistencia.poliglota.events;

/**
 * Evento de dominio emitido cuando una factura es marcada como PAGADA.
 * Se utiliza para desacoplar el flujo financiero (SQL) de la ejecución técnica (Mongo/Cassandra).
 */
public class FacturaPagadaEvent {

    private final Integer facturaId;
    private final Integer usuarioId;
    private final String descripcionProceso;
    private final String procesoId; // 🔗 vínculo técnico con Mongo

    public FacturaPagadaEvent(Integer facturaId, Integer usuarioId, String descripcionProceso, String procesoId) {
        this.facturaId = facturaId;
        this.usuarioId = usuarioId;
        this.descripcionProceso = descripcionProceso;
        this.procesoId = procesoId;
    }

    public Integer getFacturaId() {
        return facturaId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getDescripcionProceso() {
        return descripcionProceso;
    }

    public String getProcesoId() {
        return procesoId;
    }
}
