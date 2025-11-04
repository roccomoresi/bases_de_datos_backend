package com.example.persistencia.poliglota.events;

/**
 * Evento de dominio emitido cuando una factura es marcada como PAGADA.
 * Se utiliza para desacoplar el flujo financiero (SQL) de la ejecución técnica (Mongo/Cassandra).
 */
public class FacturaPagadaEvent {

    private final Integer facturaId;
    private final Integer usuarioId;
    private final String descripcionProceso;

    public FacturaPagadaEvent(Integer facturaId, Integer usuarioId, String descripcionProceso) {
        this.facturaId = facturaId;
        this.usuarioId = usuarioId;
        this.descripcionProceso = descripcionProceso;
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
}

