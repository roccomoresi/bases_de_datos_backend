package com.example.persistencia.poliglota.dto;

import com.example.persistencia.poliglota.model.mongo.Proceso;

public class ProcesoMapper {
    public static ProcesoResponse toResponse(Proceso p) {
        return new ProcesoResponse(
            p.getId(),
            p.getNombre(),
            p.getDescripcion(),
            p.getTipo(),
            p.getCosto(),
            p.isActivo()
        );
    }
}
