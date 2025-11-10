package com.example.persistencia.poliglota.model.sql;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EstadoSesion {
        
    ACTIVA,
    INACTIVA;
        @JsonCreator
    public static EstadoSesion fromString(String value) {
        return EstadoSesion.valueOf(value.trim().toUpperCase());
    }
}
