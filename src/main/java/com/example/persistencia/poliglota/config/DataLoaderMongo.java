// package com.example.persistencia.poliglota.config;

// import com.example.persistencia.poliglota.model.mongo.Proceso;
// import com.example.persistencia.poliglota.model.mongo.SolicitudProceso;
// import com.example.persistencia.poliglota.repository.mongo.ProcesoRepository;
// import com.example.persistencia.poliglota.repository.mongo.SolicitudProcesoRepository;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Configuration;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// @Configuration
// public class DataLoaderMongo implements CommandLineRunner {

//     private final ProcesoRepository procesoRepository;
//     private final SolicitudProcesoRepository solicitudProcesoRepository;

//     public DataLoaderMongo(ProcesoRepository procesoRepository,
//                            SolicitudProcesoRepository solicitudProcesoRepository) {
//         this.procesoRepository = procesoRepository;
//         this.solicitudProcesoRepository = solicitudProcesoRepository;
//     }

//     @Override
//     public void run(String... args) {
//         System.out.println("🔄 Cargando procesos y solicitudes de ejemplo (sin sensorId)...");

//         // ---------------------------
//         // 🔹 Procesos iniciales
//         // ---------------------------
//         if (procesoRepository.count() == 0) {
//             List<Proceso> procesos = List.of(
//                 new Proceso("Informe Climático Diario",
//                         "Genera un reporte con temperaturas máximas y mínimas por ciudad",
//                         "informe",
//                         new BigDecimal("200.00")),
//                 new Proceso("Informe Mensual de Humedad",
//                         "Promedios mensuales de humedad agrupados por zonas",
//                         "informe",
//                         new BigDecimal("220.00")),
//                 new Proceso("Alerta de Temperatura Extrema",
//                         "Detecta sensores con temperaturas fuera de rango",
//                         "alerta",
//                         new BigDecimal("150.00")),
//                 new Proceso("Servicio de Consulta en Línea",
//                         "Permite consultar las lecturas de sensores en tiempo real",
//                         "servicio",
//                         new BigDecimal("250.00")),
//                 new Proceso("Análisis de Datos Históricos",
//                         "Analiza tendencias históricas de temperatura y humedad",
//                         "analisis",
//                         new BigDecimal("300.00")),
//                 new Proceso("Reporte de Anomalías",
//                         "Detecta comportamientos inusuales en las mediciones",
//                         "analisis",
//                         new BigDecimal("270.00")),
//                 new Proceso("Predicción Meteorológica",
//                         "Genera una predicción de valores futuros",
//                         "prediccion",
//                         new BigDecimal("350.00")),
//                 new Proceso("Informe Global",
//                         "Integra datos de todos los sensores a nivel mundial",
//                         "informe",
//                         new BigDecimal("400.00")),
//                 new Proceso("Control de Funcionamiento",
//                         "Evalúa el estado operativo de todos los sensores del sistema",
//                         "servicio",
//                         new BigDecimal("180.00"))
//             );

//             procesoRepository.saveAll(procesos);
//             System.out.println("✅ Se cargaron " + procesos.size() + " procesos iniciales en MongoDB.");

//             // ---------------------------
//             // 🔹 Solicitudes de procesos
//             // ---------------------------
//             if (solicitudProcesoRepository.count() == 0) {
//                 UUID usuario1 = UUID.randomUUID();
//                 UUID usuario2 = UUID.randomUUID();
//                 UUID usuario3 = UUID.randomUUID();

//                 SolicitudProceso s1 = new SolicitudProceso();
//                 s1.setId(UUID.randomUUID());
//                 s1.setUsuarioId(usuario1);
//                 s1.setProceso(procesos.get(0)); // Informe Climático Diario
//                 s1.setFechaSolicitud(LocalDateTime.now().minusHours(6));
//                 s1.setEstado("pendiente");

//                 SolicitudProceso s2 = new SolicitudProceso();
//                 s2.setId(UUID.randomUUID());
//                 s2.setUsuarioId(usuario2);
//                 s2.setProceso(procesos.get(3)); // Servicio de Consulta en Línea
//                 s2.setFechaSolicitud(LocalDateTime.now().minusHours(3));
//                 s2.setEstado("en_curso");

//                 SolicitudProceso s3 = new SolicitudProceso();
//                 s3.setId(UUID.randomUUID());
//                 s3.setUsuarioId(usuario3);
//                 s3.setProceso(procesos.get(6)); // Predicción Meteorológica
//                 s3.setFechaSolicitud(LocalDateTime.now().minusHours(1));
//                 s3.setEstado("completado");
//                 s3.setResultado("Informe generado correctamente con datos de Cassandra.");

//                 solicitudProcesoRepository.saveAll(List.of(s1, s2, s3));
//                 System.out.println("✅ Se cargaron 3 solicitudes de proceso de ejemplo.");
//             }
//         } else {
//             System.out.println("ℹ️ Procesos ya existentes, no se insertaron nuevos.");
//         }
//     }
// }
