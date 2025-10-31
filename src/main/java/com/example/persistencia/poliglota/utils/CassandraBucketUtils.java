package com.example.persistencia.poliglota.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 🧮 Utilidad para calcular los "buckets" mensuales usados en Cassandra.
 * 
 * Cada bucket representa un año y mes en formato "yyyy-MM"
 * y permite optimizar consultas de rango temporal sin recorrer toda la tabla.
 *
 * Ejemplo:
 *   Si el rango es 2025-01-15 → 2025-03-02
 *   ➜ buckets = ["2025-01", "2025-02", "2025-03"]
 */
public class CassandraBucketUtils {

    private static final DateTimeFormatter BUCKET_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calcula todos los buckets mensuales entre dos fechas (inclusive).
     *
     * @param desde Fecha de inicio (LocalDate)
     * @param hasta Fecha de fin (LocalDate)
     * @return Lista de strings con formato "yyyy-MM"
     */
    public static List<String> calcularBuckets(LocalDate desde, LocalDate hasta) {
        List<String> buckets = new ArrayList<>();

        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }

        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }

        LocalDate actual = desde.withDayOfMonth(1); // normalizar al inicio del mes
        LocalDate fin = hasta.withDayOfMonth(1);

        while (!actual.isAfter(fin)) {
            buckets.add(actual.format(BUCKET_FORMATTER));
            actual = actual.plusMonths(1);
        }

        return buckets;
    }
}
