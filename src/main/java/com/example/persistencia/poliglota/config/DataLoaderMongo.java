package com.example.persistencia.poliglota.config;

import com.example.persistencia.poliglota.model.mongo.Proceso;
import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataLoaderMongo implements CommandLineRunner {

    private final ProcesoRepository procesoRepository;
    private final SolicitudProcesoRepository solicitudProcesoRepository;

    public DataLoaderMongo(ProcesoRepository procesoRepository,
                           SolicitudProcesoRepository solicitudProcesoRepository) {
        this.procesoRepository = procesoRepository;
        this.solicitudProcesoRepository = solicitudProcesoRepository;
    }

    @Override
    public void run(String... args) {
System.out.println("🔄 Ejecutando DataLoaderMongo...");
        // ---------------------------
        // 🔹 CARGA DE PROCESOS
        // ---------------------------
        if (procesoRepository.count() == 0) {
            List<Proceso> procesos = List.of(
                new Proceso("Informe Climático Diario", "Reporte con máximas y mínimas por ciudad", "informe", new BigDecimal("150.00")),
                new Proceso("Informe Mensual de Humedad", "Promedios mensuales de humedad por zona", "informe", new BigDecimal("220.00")),
                new Proceso("Alerta de Temperatura Extrema", "Notifica temperaturas fuera de rango", "alerta", new BigDecimal("90.00")),
                new Proceso("Servicio de Consulta en Línea", "Consulta de datos de sensores en tiempo real", "servicio", new BigDecimal("180.00")),
                new Proceso("Análisis de Datos Anual", "Análisis de tendencias históricas", "analisis", new BigDecimal("300.00")),
                new Proceso("Reporte de Anomalías", "Detecta comportamientos inusuales de sensores", "analisis", new BigDecimal("250.00")),
                new Proceso("Generación de Informe Regional", "Reporte agrupado por regiones o países", "informe", new BigDecimal("270.00")),
                new Proceso("Control de Funcionamiento", "Proceso automático de revisión de sensores", "servicio", new BigDecimal("120.00")),
                new Proceso("Predicción Meteorológica", "Modelo predictivo de temperatura y humedad", "prediccion", new BigDecimal("400.00"))
            );

            procesoRepository.saveAll(procesos);
            System.out.println("✅ Se cargaron " + procesos.size() + " procesos iniciales en MongoDB.");

            // ---------------------------
            // 🔹 CARGA DE SOLICITUDES DE PROCESO
            // ---------------------------

            if (solicitudProcesoRepository.count() == 0) {
                UUID usuario1 = UUID.randomUUID();
                UUID usuario2 = UUID.randomUUID();
                UUID usuario3 = UUID.randomUUID();

                SolicitudProceso s1 = new SolicitudProceso();
                s1.setId(UUID.randomUUID());
                s1.setUsuarioId(usuario1);
                s1.setProceso(procesos.get(0)); // Informe Climático Diario
                s1.setFechaSolicitud(LocalDateTime.now().minusDays(2));
                s1.setEstado("pendiente");

                SolicitudProceso s2 = new SolicitudProceso();
                s2.setId(UUID.randomUUID());
                s2.setUsuarioId(usuario2);
                s2.setProceso(procesos.get(3)); // Servicio de Consulta en Línea
                s2.setFechaSolicitud(LocalDateTime.now().minusDays(1));
                s2.setEstado("en_curso");

                SolicitudProceso s3 = new SolicitudProceso();
                s3.setId(UUID.randomUUID());
                s3.setUsuarioId(usuario3);
                s3.setProceso(procesos.get(8)); // Predicción Meteorológica
                s3.setFechaSolicitud(LocalDateTime.now().minusHours(6));
                s3.setEstado("completado");
                s3.setResultado("Predicción generada correctamente.");

                solicitudProcesoRepository.saveAll(List.of(s1, s2, s3));
                System.out.println("✅ Se cargaron 3 solicitudes de proceso de ejemplo en MongoDB.");
            } else {
                System.out.println("ℹ️ Colección 'solicitudes_proceso' ya contiene datos, no se insertaron solicitudes iniciales.");
            }

        } else {
            System.out.println("ℹ️ Colección 'procesos' ya contenía datos, no se insertaron procesos iniciales.");
        }
    }
}
